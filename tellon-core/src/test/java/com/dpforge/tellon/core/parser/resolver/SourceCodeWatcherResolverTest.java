package com.dpforge.tellon.core.parser.resolver;

import com.dpforge.tellon.core.parser.SourceCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SourceCodeWatcherResolverTest {

    @Test
    public void resolveLiteralSingle() throws Exception {
        final SourceCodeWatcherResolver resolver = new SourceCodeWatcherResolver(
                qualifiedName -> SourceCode.createFromContent(""));
        assertEquals("test-test", resolver.resolveLiteralSingle("test-test"));
    }

    @Test
    public void resolveReferenceSingle() throws Exception {
        final SourceCodeWatcherResolver resolver = new SourceCodeWatcherResolver(qualifiedName -> {
            assertEquals("com.test.Contacts", qualifiedName);
            return SourceCode.createFromContent(
                    "class Contact {",
                    "    public static final String DEVELOPER = \"developer@example.com\";",
                    "}"
            );
        });
        assertEquals("developer@example.com",
                resolver.resolveReferenceSingle("com.test.Contacts", "DEVELOPER"));
    }

    @Test
    public void nullAddress() throws Exception {
        final SourceCodeWatcherResolver resolver = new SourceCodeWatcherResolver(qualifiedName -> {
            assertEquals("com.test.Contacts", qualifiedName);
            return SourceCode.createFromContent(
                    "class Contact {",
                    "}"
            );
        });
        try {
            resolver.resolveReferenceSingle("com.test.Contacts", "DEVELOPER");
            fail("No exception thrown");
        } catch (Exception e) {
            assertEquals("Address for field 'DEVELOPER' is null", e.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSourceCodeProvider() {
        //noinspection ConstantConditions
        new SourceCodeWatcherResolver(null);
    }

}