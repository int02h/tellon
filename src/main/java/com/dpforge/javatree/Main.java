package com.dpforge.javatree;

import com.dpforge.tellon.app.FileWalker;
import com.dpforge.tellon.app.config.AppConfig;
import com.dpforge.tellon.app.config.AppConfigReader;
import com.dpforge.tellon.app.config.ProjectConfig;
import com.dpforge.tellon.core.Changes;
import com.dpforge.tellon.core.ChangesBuilder;
import com.dpforge.tellon.core.parser.AnnotatedBlock;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.provider.SourceCodeProvider;

import java.io.File;
import java.io.IOException;
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

        final AppConfig config = AppConfigReader.read("/Users/d.popov/Desktop/tellon.xml");
        for (ProjectConfig project : config.getProjects()) {
            processProject(project.getPath(), sourceCodeProvider);
        }
    }

    private static void processProject(String path, SourceCodeProvider provider) throws IOException {
        final FileWalker walker = FileWalker.create(path);
        final ChangesBuilder changesBuilder = new ChangesBuilder();

        File file;
        while ((file = walker.nextFile()) != null) {
            final SourceCode actual = provider.getActual(file);
            final SourceCode prev = provider.getPrevious(file);
            final Changes changes = changesBuilder.build(prev, actual);

            System.out.format("File '%s'\n", file);

            if (changes.hasAdded()) {
                System.out.println("Added:");
                for (AnnotatedBlock block : changes.getAdded()) {
                    System.out.println(block.toString());
                }
                System.out.println();
            }

            if (changes.hasDeleted()) {
                System.out.println("Deleted:");
                for (AnnotatedBlock block : changes.getDeleted()) {
                    System.out.println(block.toString());
                }
                System.out.println();
            }

            if (changes.hasUpdated()) {
                System.out.println("Updated:");
                for (Changes.Update update : changes.getUpdated()) {
                    System.out.format("\tOld block: %s\n", update.getOldBlock());
                    System.out.format("\tNew block: %s\n", update.getNewBlock());
                }
                System.out.println();
            }
        }
    }
}
