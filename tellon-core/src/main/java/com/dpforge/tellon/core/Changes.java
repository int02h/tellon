package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.AnnotatedBlock;

import java.util.ArrayList;
import java.util.List;

public class Changes {
    private final List<Update> updated = new ArrayList<>();
    private final List<AnnotatedBlock> deleted = new ArrayList<>();
    private final List<AnnotatedBlock> added = new ArrayList<>();

    Changes() {
    }

    void addChanged(AnnotatedBlock oldBlock, AnnotatedBlock newBlock) {
        updated.add(new Update(oldBlock, newBlock));
    }

    void addDeleted(AnnotatedBlock oldBlock) {
        deleted.add(oldBlock);
    }

    void addInserted(AnnotatedBlock newBlock) {
        added.add(newBlock);
    }

    public boolean isEmpty() {
        return !hasUpdated() && !hasDeleted() && !hasAdded();
    }

    public boolean hasUpdated() {
        return !updated.isEmpty();
    }

    public boolean hasDeleted() {
        return !deleted.isEmpty();
    }

    public boolean hasAdded() {
        return !added.isEmpty();
    }

    public List<Update> getUpdated() {
        return new ArrayList<>(updated);
    }

    public List<AnnotatedBlock> getDeleted() {
        return new ArrayList<>(deleted);
    }

    public List<AnnotatedBlock> getAdded() {
        return new ArrayList<>(added);
    }

    public static class Update {
        private final AnnotatedBlock oldBlock;
        private final AnnotatedBlock newBlock;

        public Update(AnnotatedBlock oldBlock, AnnotatedBlock newBlock) {
            this.oldBlock = oldBlock;
            this.newBlock = newBlock;
        }

        public AnnotatedBlock getOldBlock() {
            return oldBlock;
        }

        public AnnotatedBlock getNewBlock() {
            return newBlock;
        }
    }
}
