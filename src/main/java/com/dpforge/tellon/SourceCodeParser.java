package com.dpforge.tellon;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class SourceCodeParser {
    private SourceCodeParser() {
    }

    public static SourceCode parse(final File file) throws FileNotFoundException {
        return parse(JavaParser.parse(file));
    }

    public static SourceCode parse(final String code) {
        return parse(JavaParser.parse(code));
    }

    private static SourceCode parse(CompilationUnit unit) {
        final VisitorContext visitorContext = new VisitorContext();
        new Visitor().visit(unit, visitorContext);
        return new SourceCode(visitorContext.getAnnotatedBlocks());
    }

    private static class Visitor extends VoidVisitorAdapter<VisitorContext> {
        @Override
        public void visit(MethodDeclaration declaration, VisitorContext visitorContext) {
            final String[] values = visitorContext.getAnnotationExtractor().tryExtract(declaration);
            if (values != null) {
                System.out.format("Method '%s' annotated with '%s'\n", declaration.getDeclarationAsString(), Arrays.toString(values));
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(ConstructorDeclaration declaration, VisitorContext visitorContext) {
            final String[] values = visitorContext.getAnnotationExtractor().tryExtract(declaration);
            if (values != null) {
                System.out.format("Constructor '%s' annotated with '%s'\n", declaration.getDeclarationAsString(), Arrays.toString(values));
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(FieldDeclaration declaration, VisitorContext visitorContext) {
            final String[] values = visitorContext.getAnnotationExtractor().tryExtract(declaration);
            if (values != null) {
                for (VariableDeclarator var : declaration.getVariables()) {
                    System.out.format("Field '%s' annotated with '%s'\n", var.getName(), Arrays.toString(values));
                }
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration));
            }
            super.visit(declaration, visitorContext);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration declaration, VisitorContext visitorContext) {
            final String[] values = visitorContext.getAnnotationExtractor().tryExtract(declaration);
            if (values != null) {
                System.out.format("Type '%s' annotated with '%s'\n", declaration.getName(), Arrays.toString(values));
                visitorContext.addAnnotatedBlock(AnnotatedBlock.fromNode(declaration));
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
