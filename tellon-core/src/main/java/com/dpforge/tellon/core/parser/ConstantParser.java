package com.dpforge.tellon.core.parser;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class ConstantParser {

    public Map<String, List<String>> parse(SourceCode sourceCode) {
        final Map<String, List<String>> fields = new HashMap<>();
        new Visitor().visit(sourceCode.toCompilationUnit(), fields);
        return fields;
    }

    private static class Visitor extends VoidVisitorAdapter<Map<String, List<String>>> {
        @Override
        public void visit(FieldDeclaration field, Map<String, List<String>> map) {
            for (VariableDeclarator var : field.getVariables()) {
                final String name = var.getNameAsString();
                if (!var.getInitializer().isPresent()) {
                    throw new RuntimeException("Field '" + name + "' has no initializer");
                }
                final Expression initializer = var.getInitializer().get();
                if (initializer instanceof StringLiteralExpr) {
                    final String value = ((StringLiteralExpr) initializer).getValue();
                    if (map.put(name, Collections.singletonList(value)) != null) {
                        throw new RuntimeException("Field '" + name + "' initialized more than once");
                    }
                } else if (initializer instanceof ArrayInitializerExpr) {
                    map.put(name, processArrayExpression((ArrayInitializerExpr) initializer));
                } else {
                    throw new RuntimeException("Field '" + name + "' must be initialized with string literal or string literal array");
                }
            }
            super.visit(field, map);
        }

        private List<String> processArrayExpression(ArrayInitializerExpr expression) {
            final NodeList<Expression> values = expression.getValues();
            final List<String> result = new ArrayList<>(values.size());
            for (Expression val : values) {
                if (val instanceof StringLiteralExpr) {
                    result.add(((StringLiteralExpr) val).getValue());
                } else {
                    throw new UnsupportedOperationException();
                }
            }
            return result;
        }
    }
}
