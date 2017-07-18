package com.dpforge.tellon.core.observer;

import org.junit.Test;

import static org.junit.Assert.*;

public class RevisionTest {
    @Test
    public void getVersion() throws Exception {
        final Revision revision = new Revision.Builder("v0.1.2").build();
        assertEquals("v0.1.2", revision.getVersion());
    }

    @Test
    public void getAuthor() throws Exception {
        final Revision revision = new Revision.Builder("v0.1.2")
                .author("John Smith")
                .build();
        assertEquals("John Smith", revision.getAuthor());
    }

}