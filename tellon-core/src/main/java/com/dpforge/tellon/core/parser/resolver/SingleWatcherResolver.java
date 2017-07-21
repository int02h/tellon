package com.dpforge.tellon.core.parser.resolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class SingleWatcherResolver implements WatcherResolver {
    @Override
    public List<String> resolveLiteral(final String value) throws IOException {
        return Collections.singletonList(resolveLiteralSingle(value));
    }

    @Override
    public List<String> resolveReference(final String qualifiedName, final String field) throws IOException {
        return Collections.singletonList(resolveReferenceSingle(qualifiedName, field));
    }

    protected abstract String resolveLiteralSingle(final String value) throws IOException;

    protected abstract String resolveReferenceSingle(String qualifiedName, String field) throws IOException;
}
