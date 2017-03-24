package com.dpforge.javatree;

import com.dpforge.tellon.app.FileWalker;
import com.dpforge.tellon.app.config.AppConfig;
import com.dpforge.tellon.app.config.AppConfigReader;
import com.dpforge.tellon.app.config.ProjectConfig;
import com.dpforge.tellon.core.Changes;
import com.dpforge.tellon.core.ChangesBuilder;
import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ProjectInfo;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.provider.SourceCodeProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class Main {

    public static void main(String[] args) throws IOException {
        SourceCodeProvider sourceCodeProvider = null;
        for (SourceCodeProvider provider : ServiceLoader.load(SourceCodeProvider.class)) {
            if (sourceCodeProvider == null) {
                sourceCodeProvider = provider;
            } else {
                throw new IllegalArgumentException("Too many source code providers");
            }
        }

        if (sourceCodeProvider == null) {
            throw new IllegalArgumentException("No source code provider found");
        }

        final Notifiers notifiers = new Notifiers();
        for (ChangesNotifier notifier : ServiceLoader.load(ChangesNotifier.class)) {
            notifiers.add(notifier);
        }

        if (notifiers.isEmpty()) {
            throw new IllegalArgumentException("No changes notifier found");
        }

        final AppConfig config = AppConfigReader.read("/Users/d.popov/Desktop/tellon.xml");
        for (ProjectConfig project : config.getProjects()) {
            notifiers.onStartProject(new ProjectInfo(project.getName(), project.getPath()));
            processProject(project.getPath(), sourceCodeProvider, notifiers);
            notifiers.onFinishedProject();
        }
    }

    private static void processProject(String path, SourceCodeProvider provider, Notifiers notifiers) throws IOException {
        final FileWalker walker = FileWalker.create(path);
        final ChangesBuilder changesBuilder = new ChangesBuilder();

        File file;
        while ((file = walker.nextFile()) != null) {
            final SourceCode actual = provider.getActual(file);
            final SourceCode prev = provider.getPrevious(file);
            final Changes changes = changesBuilder.build(file, prev, actual);
            notifiers.notifyChanges(changes);
        }
    }

    private static class Notifiers implements ChangesNotifier {
        private final List<ChangesNotifier> list = new ArrayList<>();

        void add(ChangesNotifier notifier) {
            list.add(notifier);
        }

        boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public String getName() {
            return "compound";
        }

        @Override
        public String getDescription() {
            return getName();
        }

        @Override
        public void onStartProject(ProjectInfo projectInfo) {
            for (ChangesNotifier notifier : list) {
                notifier.onStartProject(projectInfo);
            }
        }

        @Override
        public void onFinishedProject() {
            for (ChangesNotifier notifier : list) {
                notifier.onFinishedProject();
            }
        }

        @Override
        public void notifyChanges(Changes changes) {
            for (ChangesNotifier notifier : list) {
                notifier.notifyChanges(changes);
            }
        }
    }
}
