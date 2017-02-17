package com.dpforge.tellon;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class AnnotatedBlock {
    private final SourceCodePosition startPosition;
    private final SourceCodePosition endPosition;
    private final String body;
    private final BlockType type;

    AnnotatedBlock(Builder builder) {
        this.startPosition = builder.startPosition;
        this.endPosition = builder.endPosition;
        this.body = builder.body;
        this.type = builder.type;
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

    @Override
    public String toString() {
        return type + " " + startPosition + " - " + endPosition;
    }

    static AnnotatedBlock fromNode(final ClassOrInterfaceDeclaration node) {
        return create(node, BlockType.CLASS_OR_INTERFACE);
    }

    static AnnotatedBlock fromNode(final ConstructorDeclaration node) {
        return create(node, BlockType.CONSTRUCTOR);
    }

    static AnnotatedBlock fromNode(final MethodDeclaration node) {
        return create(node, BlockType.METHOD);
    }

    static AnnotatedBlock fromNode(final FieldDeclaration node) {
        return create(node, BlockType.FIELD);
    }

    private static AnnotatedBlock create(final Node node, final BlockType type) {
        SourceCodePosition start = node.getBegin().isPresent()
                ? SourceCodePosition.create(node.getBegin().get())
                : null;
        SourceCodePosition end = node.getEnd().isPresent()
                ? SourceCodePosition.create(node.getEnd().get())
                : null;
        return new Builder(node.toString(), type)
                .startPosition(start)
                .endPosition(end)
                .build();
    }

    private static class Builder {
        private final String body;
        private final BlockType type;

        private SourceCodePosition startPosition;
        private SourceCodePosition endPosition;

        Builder(String body, BlockType type) {
            this.body = body;
            this.type = type;
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
