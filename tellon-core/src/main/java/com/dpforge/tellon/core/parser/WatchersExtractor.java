package com.dpforge.tellon.core.parser;

import com.dpforge.tellon.annotations.NotifyChanges;
import com.dpforge.tellon.core.parser.resolver.WatcherResolver;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class WatchersExtractor {
    private static final String ANNOTATION_NAME = NotifyChanges.class.getSimpleName();
    private static final String ANNOTATION_QUALIFIED_NAME = NotifyChanges.class.getName();

    private final VisitorContext visitorContext;
    private final WatcherResolver watcherResolver;

    WatchersExtractor(VisitorContext visitorContext, WatcherResolver watcherResolver) {
        this.visitorContext = visitorContext;
        this.watcherResolver = watcherResolver;
    }

    List<String> tryExtractWatchers(NodeWithAnnotations<?> node) {
        final NodeList<AnnotationExpr> annotations = node.getAnnotations();

        if (annotations.isEmpty()) {
            return null;
        }

        for (AnnotationExpr a : annotations) {
            if (verifyAnnotation(a)) {
                try {
                    return Collections.unmodifiableList(extractArguments(a));
                } catch (IOException e) {
                    throw new RuntimeException("Fail to extract watchers", e);
                }
            }
        }
        return null;
    }

    private boolean verifyAnnotation(AnnotationExpr annotation) {
        if (ANNOTATION_QUALIFIED_NAME.equals(annotation.getNameAsString())) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (visitorContext.isAnnotationImported()) {
            return ANNOTATION_NAME.equals(annotation.getNameAsString());
        }

        return false;
    }

    private List<String> extractArguments(AnnotationExpr annotation) throws IOException {
        if (annotation instanceof SingleMemberAnnotationExpr) {
            return processArgumentExpression(((SingleMemberAnnotationExpr) annotation).getMemberValue());
        }
        throw new UnsupportedOperationException();
    }

    private List<String> processArgumentExpression(Expression expression) throws IOException {
        if (expression instanceof ArrayInitializerExpr) {
            return processArrayExpression((ArrayInitializerExpr) expression);
        } else if (expression instanceof StringLiteralExpr) {
            final String value = ((StringLiteralExpr) expression).getValue();
            return watcherResolver.resolveLiteral(value);
        } else if (expression instanceof FieldAccessExpr) {
            return processFieldAccess((FieldAccessExpr) expression);
        }
        throw new UnsupportedOperationException();
    }

    private List<String> processArrayExpression(ArrayInitializerExpr expression) throws IOException {
        final NodeList<Expression> values = expression.getValues();
        final List<String> result = new ArrayList<>(values.size());
        for (Expression val : values) {
            if (val instanceof StringLiteralExpr) {
                String value = ((StringLiteralExpr) val).getValue();
                result.addAll(watcherResolver.resolveLiteral(value));
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return result;
    }

    private List<String> processFieldAccess(FieldAccessExpr expression) throws IOException {
        if (!expression.getScope().isPresent()) {
            throw new RuntimeException("Field comes without class name: " + expression.getNameAsString());
        }
        final String className = expression.getScope().get().toString();
        final String fieldName = expression.getNameAsString();
        final String qualifiedName = visitorContext.resolveClassName(className);
        if (qualifiedName == null || qualifiedName.isEmpty()) {
            throw new RuntimeException("Class '" + className + "' is not imported or imported in unsupported way");
        }
        return watcherResolver.resolveReference(qualifiedName, fieldName);
    }
}
