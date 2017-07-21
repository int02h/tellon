package com.dpforge.tellon.core.parser.resolver;

import org.junit.Test;

import static org.junit.Assert.*;

public class AsIsWatcherResolverTest {
    @Test
    public void resolveLiteralSingle() throws Exception {
        final AsIsWatcherResolver resolver = new AsIsWatcherResolver();
        assertEquals("hello world", resolver.resolveLiteralSingle("hello world"));
    }

    @Test
    public void resolveReferenceSingle() throws Exception {
        final AsIsWatcherResolver resolver = new AsIsWatcherResolver();
        assertEquals("Hello.World", resolver.resolveReferenceSingle("Hello", "World"));
    }

}