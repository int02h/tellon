package com.dpforge.tellon.core.parser;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.Collections;
import java.util.EnumSet;

public class AnnotatedBlockHelper {
    public static AnnotatedBlock createBlock() {
        final ClassOrInterfaceDeclaration node = new ClassOrInterfaceDeclaration(
                EnumSet.of(Modifier.PUBLIC),
                true,
                "Bar");
        node.setRange(new Range(new Position(1, 1), new Position(2, 1)));
        return AnnotatedBlock.fromNode(
                SourceCode.createFromContent("class Bar {", "}"),
                node,
                Collections.emptyList());
    }
}
