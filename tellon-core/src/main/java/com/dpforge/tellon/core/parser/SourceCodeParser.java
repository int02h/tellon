package com.dpforge.tellon.core.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.IOException;

public class SourceCodeParser {
    public SourceCodeParser() {
    }

    public ParsedSourceCode parse(SourceCode sourceCode) throws IOException {
        return parse(sourceCode.toCompilationUnit());
    }

    /**
     * for tests
     */
    ParsedSourceCode parse(final String code) {
        return parse(JavaParser.parse(code));
    }

    private ParsedSourceCode parse(CompilationUnit unit) {
        final VisitorContext visitorContext = new VisitorContext();
        new Visitor().visit(unit, visitorContext);
        return new ParsedSourceCode(visitorContext.getAnnotatedBlocks());
    }

    private static class Visitor extends VoidVisitorAdapter<VisitorContext> {
        @Override
        public void visit(MethodDeclaration declaration, VisitorContext visitorContext) {
            final WatcherList watcherList = visitorContext.getWatchersExtractor().tryExtractWatchers(declaration);
            if (watcherList != null) {
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration, watcherList));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(ConstructorDeclaration declaration, VisitorContext visitorContext) {
            final WatcherList watcherList = visitorContext.getWatchersExtractor().tryExtractWatchers(declaration);
            if (watcherList != null) {
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration, watcherList));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(FieldDeclaration declaration, VisitorContext visitorContext) {
            final WatcherList watchers = visitorContext.getWatchersExtractor().tryExtractWatchers(declaration);
            if (watchers != null) {
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration, watchers));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration declaration, VisitorContext visitorContext) {
            final WatcherList watchers = visitorContext.getWatchersExtractor().tryExtractWatchers(declaration);
            if (watchers != null) {
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration, watchers));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(AnnotationDeclaration declaration, VisitorContext visitorContext) {
            final WatcherList watchers = visitorContext.getWatchersExtractor().tryExtractWatchers(declaration);
            if (watchers != null) {
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration, watchers));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(AnnotationMemberDeclaration declaration, VisitorContext visitorContext) {
            final WatcherList watchers = visitorContext.getWatchersExtractor().tryExtractWatchers(declaration);
            if (watchers != null) {
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration, watchers));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(ImportDeclaration declaration, VisitorContext visitorContext) {
            visitorContext.addImport(declaration);
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(PackageDeclaration declaration, VisitorContext visitorContext) {
            visitorContext.setPackage(declaration);
            super.visit(declaration, visitorContext);
        }
    }
}
