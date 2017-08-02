package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.AnnotatedBlock;
import com.dpforge.tellon.core.parser.AnnotatedBlockHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChangesTest {
    @Test
    public void updated() {
        final Changes changes = new Changes();
        final AnnotatedBlock oldBblock = AnnotatedBlockHelper.createBlock();
        final AnnotatedBlock newBlock = AnnotatedBlockHelper.createBlock();
        changes.addChanged(oldBblock, newBlock);

        assertFalse(changes.isEmpty());
        assertTrue(changes.hasUpdated());

        assertEquals(1, changes.getUpdated().size());
        final Changes.Update update = changes.getUpdated().get(0);
        assertEquals(oldBblock, update.getOldBlock());
        assertEquals(newBlock, update.getNewBlock());
    }

    @Test
    public void inserted() {
        final Changes changes = new Changes();
        final AnnotatedBlock block = AnnotatedBlockHelper.createBlock();
        changes.addInserted(block);

        assertFalse(changes.isEmpty());
        assertTrue(changes.hasAdded());
        assertEquals(1, changes.getAdded().size());
        assertEquals(block, changes.getAdded().get(0));
    }

    @Test
    public void deleted() {
        final Changes changes = new Changes();
        final AnnotatedBlock block = AnnotatedBlockHelper.createBlock();
        changes.addDeleted(block);

        assertFalse(changes.isEmpty());
        assertTrue(changes.hasDeleted());
        assertEquals(1, changes.getDeleted().size());
        assertEquals(block, changes.getDeleted().get(0));
    }

    @Test
    public void empty() {
        final Changes changes = new Changes();
        assertTrue(changes.isEmpty());
    }
}