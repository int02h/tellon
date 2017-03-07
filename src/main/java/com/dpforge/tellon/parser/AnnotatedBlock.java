package com.dpforge.tellon.parser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;

public class AnnotatedBlock {
    private final SourceCodePosition startPosition;
    private final SourceCodePosition endPosition;
    private final String body;
    private final BlockType type;
    private final String description;

    AnnotatedBlock(Builder builder) {
        this.startPosition = builder.startPosition;
        this.endPosition = builder.endPosition;
        this.body = builder.body;
        this.type = builder.type;
        this.description = builder.description;
    }

    public SourceCodePosition getStartPosition() {
        return startPosition;
    }

    public SourceCodePosition getEndPosition() {
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

    @Override
    public String toString() {
        return type + " '" + description + "' " + startPosition + " - " + endPosition;
    }

    static AnnotatedBlock fromNode(final ClassOrInterfaceDeclaration node) {
        return create(node, BlockType.TYPE, node.getNameAsString());
    }

    static AnnotatedBlock fromNode(final AnnotationDeclaration node) {
        return create(node, BlockType.ANNOTATION, node.getNameAsString());
    }

    static AnnotatedBlock fromNode(final ConstructorDeclaration node) {
        return create(node, BlockType.CONSTRUCTOR, node.getNameAsString());
    }

    static AnnotatedBlock fromNode(final MethodDeclaration node) {
        return create(node, BlockType.METHOD, node.getNameAsString());
    }

    static AnnotatedBlock fromNode(final FieldDeclaration node) {
        StringBuilder builder = new StringBuilder();
        for (VariableDeclarator var : node.getVariables()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(var.getNameAsString());
        }
        return create(node, BlockType.FIELD, builder.toString());
    }

    static AnnotatedBlock fromNode(AnnotationMemberDeclaration node) {
        return create(node, BlockType.ANNOTATION_MEMBER, node.getNameAsString());
    }

    private static AnnotatedBlock create(final Node node, final BlockType type, final String description) {
        final Builder builder = new Builder(node.toString(), type);

        if (description != null) {
            builder.description(description);
        }

        if (node.getBegin().isPresent()) {
            builder.startPosition(SourceCodePosition.create(node.getBegin().get()));
        }

        if (node.getEnd().isPresent()) {
            builder.endPosition(SourceCodePosition.create(node.getEnd().get()));
        }

        return builder.build();
    }

    private static class Builder {
        private final String body;
        private final BlockType type;

        private String description;
        private SourceCodePosition startPosition;
        private SourceCodePosition endPosition;

        Builder(String body, BlockType type) {
            this.body = body;
            this.type = type;
        }

        Builder description(String description) {
            this.description = description;
            return this;
        }

        Builder startPosition(SourceCodePosition position) {
            this.startPosition = position;
            return this;
        }

        Builder endPosition(SourceCodePosition position) {
            this.endPosition = position;
            return this;
        }

        AnnotatedBlock build() {
            return new AnnotatedBlock(this);
        }
    }
}
