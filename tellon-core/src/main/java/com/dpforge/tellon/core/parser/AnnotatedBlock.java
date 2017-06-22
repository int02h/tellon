package com.dpforge.tellon.core.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;

public class AnnotatedBlock {
    private final FilePosition startPosition;
    private final FilePosition endPosition;
    private final BlockSourceCode sourceCode;
    private final BlockType type;
    private final String name;
    private final WatcherList watchers;

    AnnotatedBlock(Builder builder) {
        this.startPosition = builder.startPosition;
        this.endPosition = builder.endPosition;
        this.sourceCode = builder.sourceCode;
        this.type = builder.type;
        this.name = builder.name;
        this.watchers = builder.watchers;
    }

    public FilePosition getStartPosition() {
        return startPosition;
    }

    public FilePosition getEndPosition() {
        return endPosition;
    }

    public BlockSourceCode getSourceCode() {
        return sourceCode;
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
                .type(type);

        final FilePosition startPosition;
        if (node.hasJavaDocComment()) {
            if (!node.getComment().getBegin().isPresent()) {
                throw new IllegalStateException("JavaDoc block position is unknown");
            }
            startPosition = FilePosition.create(node.getComment().getBegin().get());
        } else {
            startPosition = FilePosition.create(node.getBegin().get());
        }
        builder.startPosition(startPosition);

        final FilePosition endPosition = FilePosition.create(node.getEnd().get());
        builder.endPosition(endPosition);

        final String[] rawSourceCode = sourceCode.getContent().getExactSubset(startPosition, endPosition);
        final String[] sourceCodeFragment = sourceCode.getContent().getLineRange(startPosition, endPosition);
        builder.sourceCode(new BlockSourceCode(rawSourceCode, sourceCodeFragment));

        return builder;
    }

    private static class Builder {
        private BlockType type;
        private BlockSourceCode sourceCode;
        private String name;
        private FilePosition startPosition;
        private FilePosition endPosition;
        private WatcherList watchers;

        Builder sourceCode(BlockSourceCode sourceCode) {
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

        AnnotatedBlock build() {
            if (sourceCode == null
                    || type == null
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
