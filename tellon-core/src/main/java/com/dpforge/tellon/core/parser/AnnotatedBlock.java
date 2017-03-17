package com.dpforge.tellon.core.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;

public class AnnotatedBlock {
    private final FilePosition startPosition;
    private final FilePosition endPosition;
    private final String body;
    private final BlockType type;
    private final String description;
    private final WatcherList watchers;

    AnnotatedBlock(Builder builder) {
        this.startPosition = builder.startPosition;
        this.endPosition = builder.endPosition;
        this.body = builder.body;
        this.type = builder.type;
        this.description = builder.description;
        this.watchers = builder.watchers;
    }

    public FilePosition getStartPosition() {
        return startPosition;
    }

    public FilePosition getEndPosition() {
        return endPosition;
    }

    public String getBody() {
        return body;
    }

    public BlockType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public WatcherList getWatchers() {
        return watchers;
    }

    @Override
    public String toString() {
        return type + " '" + description + "' " + startPosition + " - " + endPosition + " " + watchers;
    }

    static AnnotatedBlock fromNode(final ClassOrInterfaceDeclaration node, final WatcherList watchers) {
        return createBuilder(node, BlockType.TYPE)
                .description(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final AnnotationDeclaration node, final WatcherList watchers) {
        return createBuilder(node, BlockType.ANNOTATION)
                .description(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final ConstructorDeclaration node, final WatcherList watchers) {
        return createBuilder(node, BlockType.CONSTRUCTOR)
                .description(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final MethodDeclaration node, final WatcherList watchers) {
        return createBuilder(node, BlockType.METHOD)
                .description(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final FieldDeclaration node, final WatcherList watchers) {
        StringBuilder builder = new StringBuilder();
        for (VariableDeclarator var : node.getVariables()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(var.getNameAsString());
        }
        return createBuilder(node, BlockType.FIELD)
                .description(builder.toString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(AnnotationMemberDeclaration node, final WatcherList watchers) {
        return createBuilder(node, BlockType.ANNOTATION_MEMBER)
                .description(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    private static Builder createBuilder(final Node node, final BlockType type) {
        final Builder builder = new Builder(node.toString(), type);

        if (node.getBegin().isPresent()) {
            builder.startPosition(FilePosition.create(node.getBegin().get()));
        }

        if (node.getEnd().isPresent()) {
            builder.endPosition(FilePosition.create(node.getEnd().get()));
        }

        return builder;
    }

    private static class Builder {
        private final String body;
        private final BlockType type;

        private String description;
        private FilePosition startPosition;
        private FilePosition endPosition;
        private WatcherList watchers;

        Builder(String body, BlockType type) {
            this.body = body;
            this.type = type;
        }

        Builder description(String description) {
            this.description = description;
            return this;
        }

        Builder startPosition(FilePosition position) {
            this.startPosition = position;
            return this;
        }

        Builder endPosition(FilePosition position) {
            this.endPosition = position;
            return this;
        }

        Builder watchers(WatcherList watchers) {
            this.watchers = watchers;
            return this;
        }

        AnnotatedBlock build() {
            return new AnnotatedBlock(this);
        }
    }
}
