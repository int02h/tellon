package com.dpforge.javatree;

import com.dpforge.tellon.SourceCodeParser;

import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        final String projectPath = "/Users/d.popov/Desktop/tellon/src/main/java";
        final String testPath = "/com/test";
        final File testFile = new File(projectPath, testPath);
        final File[] files = testFile.listFiles();

        assert files != null;
        for (File file : files) {
            System.out.println("-----[" + file.getName() + "]-----");
            SourceCodeParser.parse(file);
            System.out.println();
        }
    }
}
