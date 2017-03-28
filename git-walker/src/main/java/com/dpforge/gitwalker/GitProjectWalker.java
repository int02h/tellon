package com.dpforge.gitwalker;

import com.dpforge.tellon.core.ProjectItem;
import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.ProjectWalkerException;
import com.dpforge.tellon.core.notifier.ProjectInfo;
import com.dpforge.tellon.core.parser.SourceCode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitProjectWalker implements ProjectWalker {
    private static final String NAME = "git-walker";
    private static final String DESCRIPTION = "Tellon project walker over local git repository";

    private File gitFile;

    private ProjectInfo projectInfo;

    private List<ProjectItem> items;

    private int index;

    @Override
    public void init(String args) throws ProjectWalkerException {
        parseArguments(args);

        projectInfo = new ProjectInfo.Builder()
                .name(gitFile.getAbsolutePath()) // TODO better name
                .build();

        try {
            try (final Repository repository = new FileRepositoryBuilder()
                    .setGitDir(gitFile)
                    .build()) {
                final ObjectId newId = repository.resolve("HEAD^{tree}");
                final ObjectId oldId = repository.resolve("HEAD^^{tree}");
                List<DiffEntry> diff = buildDiff(repository, oldId, newId);
                items = buildProjectItems(repository, diff);
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

    private void parseArguments(final String args) throws ProjectWalkerException {
        final String[] values = args.split(";");
        if (values.length == 0) {
            throw new ProjectWalkerException("Path to .git directory not provided");
        }

        gitFile = new File(values[0]);
        if (!gitFile.exists()) {
            throw new ProjectWalkerException(String.format(".git folder '%s' not found", values[0]));
        }
    }

    private static List<ProjectItem> buildProjectItems(Repository repository, List<DiffEntry> diff) {
        final List<ProjectItem> items = new ArrayList<>(diff.size());
        for (DiffEntry entry : diff) {
            if (entry.getChangeType() == DiffEntry.ChangeType.MODIFY) { // TODO support another
                items.add(new ProjectItem() {
                    @Override
                    public String getDescription() {
                        return entry.toString();
                    }

                    @Override
                    public SourceCode getActual() throws IOException {
                        ObjectStream os = repository.open(entry.getNewId().toObjectId()).openStream();
                        return SourceCode.createFromContent(getStreamContent(os));
                    }

                    @Override
                    public SourceCode getPrevious() throws IOException {
                        ObjectStream os = repository.open(entry.getOldId().toObjectId()).openStream();
                        return SourceCode.createFromContent(getStreamContent(os));
                    }
                });
            }
        }
        return items;
    }

    private static String getStreamContent(ObjectStream os) throws IOException {
        final StringBuilder builder = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(os))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private static List<DiffEntry> buildDiff(Repository repo, ObjectId oldId, ObjectId newId) throws IOException, GitAPIException {
        try (ObjectReader reader = repo.newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, newId);

            // finally get the list of changed files
            try (Git git = new Git(repo)) {
                return git.diff()
                        .setNewTree(newTreeIter)
                        .setOldTree(oldTreeIter)
                        .call();
            }
        }
    }
}
