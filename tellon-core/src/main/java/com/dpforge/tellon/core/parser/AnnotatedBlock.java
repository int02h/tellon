package com.dpforge.tellon.core.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;

public class AnnotatedBlock {
    private final SourceCode sourceCode;
    private final FilePosition startPosition;
    private final FilePosition endPosition;
    private final String body;
    private final BlockType type;
    private final String name;
    private final WatcherList watchers;

    AnnotatedBlock(Builder builder) {
        this.sourceCode = builder.sourceCode;
        this.startPosition = builder.startPosition;
        this.endPosition = builder.endPosition;
        this.body = builder.body;
        this.type = builder.type;
        this.name = builder.name;
        this.watchers = builder.watchers;
    }

    public SourceCode getContainingSourceCode() {
        return sourceCode;
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

    public String getName() {
        return name;
    }

    public WatcherList getWatchers() {
        return watchers;
    }

    @Override
    public String toString() {
        return type + " '" + name + "' " + startPosition + " - " + endPosition + " " + watchers;
    }

    static AnnotatedBlock fromNode(final SourceCode sourceCode,
                                   final ClassOrInterfaceDeclaration node,
                                   final WatcherList watchers) {
        return createBuilder(sourceCode, node, BlockType.TYPE)
                .name(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final SourceCode sourceCode,
                                   final AnnotationDeclaration node,
                                   final WatcherList watchers) {
        return createBuilder(sourceCode, node, BlockType.ANNOTATION)
                .name(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final SourceCode sourceCode,
                                   final ConstructorDeclaration node,
                                   final WatcherList watchers) {
        return createBuilder(sourceCode, node, BlockType.CONSTRUCTOR)
                .name(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final SourceCode sourceCode,
                                   final MethodDeclaration node,
                                   final WatcherList watchers) {
        return createBuilder(sourceCode, node, BlockType.METHOD)
                .name(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final SourceCode sourceCode,
                                   final FieldDeclaration node,
                                   final WatcherList watchers) {
        StringBuilder builder = new StringBuilder();
        for (VariableDeclarator var : node.getVariables()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(var.getNameAsString());
        }
        return createBuilder(sourceCode, node, BlockType.FIELD)
                .name(builder.toString())
                .watchers(watchers)
                .build();
    }

    static AnnotatedBlock fromNode(final SourceCode sourceCode,
                                   final AnnotationMemberDeclaration node,
                                   final WatcherList watchers) {
        return createBuilder(sourceCode, node, BlockType.ANNOTATION_MEMBER)
                .name(node.getNameAsString())
                .watchers(watchers)
                .build();
    }

    private static Builder createBuilder(final SourceCode sourceCode, final Node node, final BlockType type) {
        if (!node.getBegin().isPresent() || !node.getEnd().isPresent()) {
            throw new IllegalStateException("Source code block position is unknown");
        }

        final Builder builder = new Builder()
                .type(type)
                .sourceCode(sourceCode);

        final FilePosition startPosition = FilePosition.create(node.getBegin().get());
        builder.startPosition(startPosition);

        final FilePosition endPosition = FilePosition.create(node.getEnd().get());
        builder.endPosition(endPosition);

        builder.body(getBody(sourceCode, startPosition, endPosition));

        return builder;
    }

    private static String getBody(final SourceCode sourceCode, final FilePosition start, final FilePosition end) {
        if (start.getLine() == end.getLine()) {
            final String line = sourceCode.getContent()[start.getLine()];
            return line.substring(start.getColumn(), end.getColumn() + 1);
        }

        final StringBuilder body = new StringBuilder();
        body.append(sourceCode.getContent()[start.getLine()].substring(start.getColumn())).append("\n");
        for (int i = start.getLine() + 1; i < end.getLine(); i++) {
            body.append(sourceCode.getContent()[i]).append("\n");
        }
        body.append(sourceCode.getContent()[end.getLine()].substring(0, end.getColumn() + 1));
        return body.toString();
    }

    private static class Builder {
        private SourceCode sourceCode;
        private BlockType type;
        private String body;
        private String name;
        private FilePosition startPosition;
        private FilePosition endPosition;
        private WatcherList watchers;

        Builder sourceCode(SourceCode sourceCode) {
            this.sourceCode = sourceCode;
            return this;
        }

        Builder type(BlockType type) {
            this.type = type;
            return this;
        }

        Builder name(String name) {
            this.name = name;
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

        Builder body(String body) {
            this.body = body;
            return this;
        }

        AnnotatedBlock build() {
            if (sourceCode == null
                    || type == null
                    || body == null
                    || name == null
                    || startPosition == null
                    || endPosition == null
                    || watchers == null) {
                throw new IllegalArgumentException("Some fields of annotated block are null");
            }
            return new AnnotatedBlock(this);
        }
    }
}
