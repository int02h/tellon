package com.dpforge.gitwalker;

import com.dpforge.tellon.core.ProjectItem;
import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.ProjectWalkerException;
import com.dpforge.tellon.core.Revision;
import com.dpforge.tellon.core.notifier.ProjectInfo;
import com.dpforge.tellon.core.parser.SourceCode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GitProjectWalker implements ProjectWalker {
    private static final String NAME = "git-walker";
    private static final String DESCRIPTION = "Tellon project walker over local git repository";

    private static final String ARG_GIT_PATH = "gitPath";
    private static final String ARG_NEW_REVISION = "newRev";
    private static final String ARG_OLD_REVISION = "oldRev";

    private File gitFile;

    private ProjectInfo projectInfo;

    private List<ProjectItem> items;

    private int index;

    private GitRevision newRev;

    private GitRevision oldRev;

    @Override
    public void init(Map<String, String> args) throws ProjectWalkerException {
        parseArguments(args);

        projectInfo = new ProjectInfo.Builder()
                .name(gitFile.getParentFile().getName())
                .build();

        try {
            try (final Repository repository = new FileRepositoryBuilder()
                    .setGitDir(gitFile)
                    .build()) {

                newRev.fillWith(repository);
                oldRev.fillWith(repository);

                if (newRev.isReady() && oldRev.isReady()) {
                    items = buildProjectItems(repository, buildDiff(repository));
                } else {
                    items = Collections.emptyList();
                }
            }
        } catch (IOException | GitAPIException e) {
            throw new ProjectWalkerException(e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    @Override
    public boolean hasNext() {
        return index < items.size();
    }

    @Override
    public ProjectItem next() {
        return items.get(index++);
    }

    private void parseArguments(final Map<String, String> args) throws ProjectWalkerException {
        final String gitPath = args.get(ARG_GIT_PATH);
        if (gitPath == null) {
            throw new ProjectWalkerException("Path to .git directory not provided");
        }

        gitFile = new File(gitPath);
        if (!gitFile.exists()) {
            throw new ProjectWalkerException(String.format(".git folder '%s' not found", gitPath));
        }

        String newRevision = args.get(ARG_NEW_REVISION);
        if (newRevision == null) {
            newRevision = "HEAD";
        }

        String oldRevision = args.get(ARG_OLD_REVISION);
        if (oldRevision == null) {
            oldRevision = newRevision + "^";
        }

        this.newRev = new GitRevision(newRevision);
        this.oldRev = new GitRevision(oldRevision);
    }

    private List<DiffEntry> buildDiff(Repository repo) throws IOException, GitAPIException {
        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldRev.treeId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, newRev.treeId);

            try (Git git = new Git(repo)) {
                return git.diff()
                        .setNewTree(newTreeIter)
                        .setOldTree(oldTreeIter)
                        .call();
            }
        }
    }

    private List<ProjectItem> buildProjectItems(Repository repository, List<DiffEntry> diff) {
        final List<ProjectItem> items = new ArrayList<>(diff.size());
        for (DiffEntry entry : diff) {
            items.add(new ProjectItem() {
                @Override
                public String getDescription() {
                    switch (entry.getChangeType()) {
                        case ADD:
                            return entry.getNewPath();
                        case MODIFY:
                        case DELETE:
                        case RENAME:
                        case COPY:
                        default:
                            return entry.getOldPath();
                    }
                }

                @Override
                public boolean hasActual() {
                    return entry.getChangeType() != DiffEntry.ChangeType.DELETE;
                }

                @Override
                public SourceCode getActual() throws IOException {
                    ObjectStream os = repository.open(entry.getNewId().toObjectId()).openStream();
                    return SourceCode.createFromContent(getStreamContent(os));
                }

                @Override
                public Revision getActualRevision() throws IOException {
                    return newRev.info;
                }

                @Override
                public boolean hasPrevious() {
                    return entry.getChangeType() != DiffEntry.ChangeType.ADD;
                }

                @Override
                public SourceCode getPrevious() throws IOException {
                    ObjectStream os = repository.open(entry.getOldId().toObjectId()).openStream();
                    return SourceCode.createFromContent(getStreamContent(os));
                }

                @Override
                public Revision getPreviousRevision() throws IOException {
                    return oldRev.info;
                }
            });
        }
        return items;
    }

    private static List<String> getStreamContent(ObjectStream os) throws IOException {
        final List<String> content = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(os))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.add(line);
            }
        }
        return content;
    }

    private static String getCommitAuthor(RevCommit commit) {
        final PersonIdent author = commit.getAuthorIdent();
        return author.getName() + " (" + author.getEmailAddress() + ")";
    }

    private static class GitRevision {
        private final String revision;
        private ObjectId treeId;
        private ObjectId commitId;
        private Revision info;

        GitRevision(String revision) {
            this.revision = revision;
        }

        void fillWith(Repository repository) throws IOException {
            treeId = repository.resolve(revision + "^{tree}");
            commitId = repository.resolve(revision + "^{commit}");

            try (RevWalk walk = new RevWalk(repository)) {
                final RevCommit newCommit = walk.parseCommit(commitId);
                info = new Revision.Builder(commitId.name())
                        .author(getCommitAuthor(newCommit))
                        .build();
            }
        }

        boolean isReady() {
            return treeId != null || commitId != null || info != null;
        }
    }
}
