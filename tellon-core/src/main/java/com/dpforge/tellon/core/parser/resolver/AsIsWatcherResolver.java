package com.dpforge.tellon.core.parser.resolver;

import java.util.Collections;
import java.util.List;

public class AsIsWatcherResolver implements WatcherResolver {

    @Override
    public List<String> resolveLiteral(String value) {
        return Collections.singletonList(value);
    }

    @Override
    public List<String> resolveReference(String qualifiedName, String field) {
        return Collections.singletonList(qualifiedName + "." + field);
    }
}
