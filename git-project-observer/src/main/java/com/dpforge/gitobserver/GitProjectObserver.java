package com.dpforge.gitobserver;

import com.dpforge.tellon.core.observer.*;
import com.dpforge.tellon.core.parser.SourceCode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GitProjectObserver implements ProjectObserver {

    private static final String NAME = "git-project-observer";
    private static final String DESCRIPTION = "Tellon project observer over local git repository";

    private static final String ARG_GIT_PATH = "gitPath";
    private static final String ARG_NEW_REVISION = "newRev";
    private static final String ARG_OLD_REVISION = "oldRev";
    private static final String ARG_SOURCE_DIR = "srcDir";

    private File gitFile;

    private File sourceDir;

    private ProjectInfo projectInfo;

    private List<ProjectItem> items;

    private GitRevision newRev;

    private GitRevision oldRev;

    @Override
    public void init(Map<String, String> args) throws ProjectObserverException {
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
            throw new ProjectObserverException(e);
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
    public ProjectWalker createWalker() {
        return new GitProjectWalker(items);
    }

    @Override
    public SourceCode getSourceCode(final String qualifiedName) throws IOException {
        final String path = qualifiedName.replace('.', File.separatorChar) + ".java";
        final File file = new File(sourceDir, path);
        return SourceCode.createFromContent(getStreamContent(new FileInputStream(file)));
    }

    private void parseArguments(final Map<String, String> args) throws ProjectObserverException {
        final String gitPath = args.get(ARG_GIT_PATH);
        if (gitPath == null) {
            throw new ProjectObserverException("Path to .git directory not provided");
        }

        gitFile = new File(gitPath);
        if (!gitFile.exists()) {
            throw new ProjectObserverException(String.format(".git directory '%s' not found", gitPath));
        }

        final String sourceDir = args.get(ARG_SOURCE_DIR);
        if (sourceDir == null) {
            throw new ProjectObserverException("Source directory not provided");
        }

        this.sourceDir = new File(gitFile, sourceDir);
        if (!this.sourceDir.exists()) {
            throw new ProjectObserverException(String.format("Source directory '%s' not found", gitPath));
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

    private List<ProjectItem> buildProjectItems(final Repository repository, List<DiffEntry> diff) {
        final List<ProjectItem> items = new ArrayList<>(diff.size());
        for (final DiffEntry entry : diff) {
            final String diffEntryPath = getDiffEntryPath(entry);
            if (shouldSkipPath(diffEntryPath)) {
                continue;
            }

            items.add(new ProjectItem() {
                @Override
                public String getDescription() {
                    return diffEntryPath;
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

    private static boolean shouldSkipPath(final String path) {
        return !path.toLowerCase().endsWith(".java");
    }

    private String getDiffEntryPath(DiffEntry entry) {
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

    private static List<String> getStreamContent(final InputStream is) throws IOException {
        final List<String> content = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.add(line);
            }
        }
        return content;
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

        private static String getCommitAuthor(RevCommit commit) {
            final PersonIdent author = commit.getAuthorIdent();
            return author.getName() + " (" + author.getEmailAddress() + ")";
        }
    }
}
