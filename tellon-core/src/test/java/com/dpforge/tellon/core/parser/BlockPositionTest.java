package com.dpforge.tellon.core.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public class BlockPositionTest {
    @Test
    public void zeroBased() {
        final BlockPosition position = BlockPosition.createZeroBased(8, 12);
        assertEquals(8, position.getLine());
        assertEquals(12, position.getColumn());

        assertEquals(9, position.getHumanLine());
        assertEquals(13, position.getHumanColumn());
    }

    @Test
    public void humanBased() {
        final BlockPosition position = BlockPosition.createHumanBased(8, 12);
        assertEquals(7, position.getLine());
        assertEquals(11, position.getColumn());

        assertEquals(8, position.getHumanLine());
        assertEquals(12, position.getHumanColumn());
    }
}