package com.dpforge.tellon;

import com.dpforge.tellon.parser.SourceCode;
import com.dpforge.tellon.parser.SourceCodeParser;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImportTest {
    @Test
    public void normalImport() {
        final String code = "package com.test;" +
                "import com.dpforge.tellon.annotations.NotifyChanges;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = SourceCodeParser.parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void asteriskImport() {
        final String code = "package com.test;" +
                "import com.dpforge.tellon.annotations.*;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = SourceCodeParser.parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void qualifiedName() {
        final String code = "package com.test;" +
                "@com.dpforge.tellon.annotations.NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = SourceCodeParser.parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void samePackage() {
        final String code = "package com.dpforge.tellon.annotations;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = SourceCodeParser.parse(code);
        assertEquals(1, sourceCode.getAnnotatedBlocks().size());
    }

    @Test
    public void wrongPackageAnnotation() {
        final String code = "package com.test;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = SourceCodeParser.parse(code);
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }

    @Test
    public void wrongImportAnnotation() {
        final String code = "package com.test;" +
                "import com.wrongpackage.NotifyChanges;" +
                "@NotifyChanges(\"someone\") class Foo {}";
        final SourceCode sourceCode = SourceCodeParser.parse(code);
        assertTrue(sourceCode.getAnnotatedBlocks().isEmpty());
    }
}
