package com.dpforge.tellon.core.parser.resolver;

import java.io.IOException;
import java.util.List;

public interface WatcherResolver {
    List<String> resolveLiteral(final String value) throws IOException;

    List<String> resolveReference(final String qualifiedName, final String field) throws IOException;
}
