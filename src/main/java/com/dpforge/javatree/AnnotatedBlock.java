package com.dpforge.javatree;

import com.github.javaparser.ast.Node;

public class AnnotatedBlock {
    private final SourceCodePosition startPosition;
    private final SourceCodePosition endPosition;
    private final String body;

    private AnnotatedBlock(SourceCodePosition startPosition, SourceCodePosition endPosition, String body) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.body = body;
    }

    static AnnotatedBlock fromNode(final Node node) {
        SourceCodePosition start = node.getBegin().isPresent()
                ? SourceCodePosition.create(node.getBegin().get())
                : null;
        SourceCodePosition end = node.getEnd().isPresent()
                ? SourceCodePosition.create(node.getEnd().get())
                : null;
        return new AnnotatedBlock(start, end, node.toString());
    }
}
