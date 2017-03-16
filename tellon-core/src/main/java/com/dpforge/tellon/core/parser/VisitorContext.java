package com.dpforge.tellon.core.parser;

import com.dpforge.tellon.annotations.NotifyChanges;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;

import java.util.ArrayList;
import java.util.List;

class VisitorContext {
    private static final String NOTIFY_CHANGES_ANNOTATION = NotifyChanges.class.getName();
    private static final String NOTIFY_CHANGED_ANNOTATION_PACKAGE = NotifyChanges.class.getPackage().getName();

    private final WatchersExtractor watchersExtractor = new WatchersExtractor(this);

    private boolean annotationImported;

    private final List<AnnotatedBlock> annotatedBlocks = new ArrayList<>();

    WatchersExtractor getWatchersExtractor() {
        return watchersExtractor;
    }

    void addAnnotatedBlock(AnnotatedBlock block) {
        annotatedBlocks.add(block);
    }

    List<AnnotatedBlock> getAnnotatedBlocks() {
        return annotatedBlocks;
    }

    void addImport(ImportDeclaration declaration) {
        if (!annotationImported) {
            if (declaration.isAsterisk()) {
                annotationImported = NOTIFY_CHANGED_ANNOTATION_PACKAGE.equals(declaration.getNameAsString());
            } else {
                annotationImported = NOTIFY_CHANGES_ANNOTATION.equals(declaration.getNameAsString());
            }
        }
    }

    void setPackage(PackageDeclaration declaration) {
         if (!annotationImported) {
             // if annotated class in the same package with NotifyChanges annotation
             annotationImported = NOTIFY_CHANGED_ANNOTATION_PACKAGE.equals(declaration.getNameAsString());
         }
    }

    boolean isAnnotationImported() {
        return annotationImported;
    }
}
