package com.dpforge.tellon.core.parser.resolver;

public class AsIsWatcherResolver extends SingleWatcherResolver {

    @Override
    public String resolveLiteralSingle(String value) {
        return value;
    }

    @Override
    public String resolveReferenceSingle(String qualifiedName, String field) {
        return qualifiedName + "." + field;
    }
}
