package com.dpforge.tellon.app;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FileWalker {
    private static final FileFilter SRC_FILTER = file -> file.isDirectory() || file.getName().endsWith(".java");

    private final File root;
    private final List<File> files = new ArrayList<>();
    private int index;

    private FileWalker(final File root) {
        this.root = root;
    }

    public File nextFile() {
        return index < files.size()
                ? files.get(index++)
                : null;
    }

    public void reset() {
        index = 0;
        walk(root, files);
    }

    private static void walk(final File root, final List<File> result) {
        File[] files = root.listFiles(SRC_FILTER);
        if (files == null) {
            return;
        }

        for (File f : files) {
            if (f.isDirectory()) {
                walk(f, result);
            } else {
                result.add(f);
            }
        }
    }

    public static FileWalker create(final String root) throws FileNotFoundException {
        if (root == null || root.length() == 0) {
            throw new NullPointerException("Root is null or empty");
        }

        final File file = new File(root);

        if (!file.exists()) {
            throw new FileNotFoundException("Directory '" + root + "' not found");
        }

        if (!file.isDirectory()) {
            throw new FileNotFoundException("File '" + root + "' is not a directory");
        }

        final FileWalker walker = new FileWalker(file);
        walker.reset();
        return walker;
    }
}
