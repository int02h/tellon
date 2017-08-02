package com.dpforge.tellon.core.observer;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectInfoTest {
    @Test
    public void name() {
        final ProjectInfo info = new ProjectInfo.Builder().name("Hello").build();
        assertEquals("Hello", info.getName());
    }
}