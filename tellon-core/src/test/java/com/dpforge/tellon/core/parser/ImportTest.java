package com.dpforge.tellon.core.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportTest {
    @Test
    public void normalImport() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "@NotifyChanges(\"someone\") class Foo {}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void asteriskImport() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.*;",
                "@NotifyChanges(\"someone\") class Foo {}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void qualifiedName() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void samePackage() {
        final ParsedSourceCode sourceCode = parse(
                "package com.dpforge.tellon.annotations;",
                "@NotifyChanges(\"someone\") class Foo {}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void wrongPackageAnnotation() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "@NotifyChanges(\"someone\") class Foo {}");
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }

    @Test
    public void wrongImportAnnotation() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "import com.wrongpackage.NotifyChanges;",
                "@NotifyChanges(\"someone\") class Foo {}");
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }

    @Test
    public void qualifiedAndNormalImport() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.NotifyChanges;",
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void qualifiedAndAsteriskImport() {
        final ParsedSourceCode sourceCode = parse(
                "package com.test;",
                "import com.dpforge.tellon.annotations.*;",
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void qualifiedAndSamePackage() {
        final ParsedSourceCode sourceCode = parse(
                "package com.dpforge.tellon.annotations;",
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}");
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    private static ParsedSourceCode parse(final String... code) {
        return new SourceCodeParser().parse(SourceCode.createFromContent(code));
    }
}
