package com.dpforge.tellon.core.parser.resolver;

import com.dpforge.tellon.core.observer.SourceCodeProvider;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.parser.WatcherConstantParser;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SourceCodeWatcherResolver implements WatcherResolver {

    private final SourceCodeProvider sourceCodeProvider;
    private final WatcherConstantParser watcherConstantParser = new WatcherConstantParser();

    public SourceCodeWatcherResolver(SourceCodeProvider sourceCodeProvider) {
        if (sourceCodeProvider == null) {
            throw new NullPointerException("Source code retriever cannot be null");
        }
        this.sourceCodeProvider = sourceCodeProvider;
    }

    @Override
    public List<String> resolveLiteral(String value) {
        return Collections.singletonList(value);
    }

    @Override
    public List<String> resolveReference(String qualifiedName, String field) throws IOException {
        final SourceCode code = sourceCodeProvider.getSourceCode(qualifiedName);
        final Map<String, String> watcherMap = watcherConstantParser.parse(code);
        final String address = watcherMap.get(field);
        verifyAddress(field, address);
        return Collections.singletonList(address);
    }

    private static void verifyAddress(final String field, final String address) {
        if (address == null) {
            throw new RuntimeException("Address for field '" + field + "' is null");
        }
    }
}
