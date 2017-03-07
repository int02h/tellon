package com.dpforge.javatree;

import com.dpforge.tellon.app.FileWalker;
import com.dpforge.tellon.app.config.AppConfig;
import com.dpforge.tellon.app.config.AppConfigReader;
import com.dpforge.tellon.app.config.ProjectConfig;
import com.dpforge.tellon.parser.AnnotatedBlock;
import com.dpforge.tellon.parser.SourceCode;
import com.dpforge.tellon.parser.SourceCodeParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final AppConfig config = AppConfigReader.read("/Users/d.popov/Desktop/tellon.xml");
        for (ProjectConfig project : config.getProjects()) {
            processProject(project.getPath());
        }
    }

    private static void processProject(String path) throws FileNotFoundException {
        final FileWalker walker = FileWalker.create(path);
        File file;
        while ((file = walker.nextFile()) != null) {
            System.out.println("-----[" + file.getName() + "]-----");
            SourceCode src = SourceCodeParser.parse(file);
            for (AnnotatedBlock block : src.getAnnotatedBlocks()) {
                System.out.println(block.toString());
            }
            System.out.println();
        }
    }
}
