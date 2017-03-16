package com.dpforge.tellon.core.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportTest {
    @Test
    public void normalImport() {
        final String code = "package com.test;" +
                "import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void asteriskImport() {
        final String code = "package com.test;" +
                "import com.dpforge.tellon.annotations.*;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void qualifiedName() {
        final String code = "package com.test;" +
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void samePackage() {
        final String code = "package com.dpforge.tellon.annotations;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void wrongPackageAnnotation() {
        final String code = "package com.test;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }

    @Test
    public void wrongImportAnnotation() {
        final String code = "package com.test;" +
                "import com.wrongpackage.NotifyChanges;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }

    @Test
    public void qualifiedAndNormalImport() {
        final String code = "package com.test;" +
                "import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void qualifiedAndAsteriskImport() {
        final String code = "package com.test;" +
                "import com.dpforge.tellon.annotations.*;" +
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void qualifiedAndSamePackage() {
        final String code = "package com.dpforge.tellon.annotations;" +
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = new SourceCodeParser().parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }
}
