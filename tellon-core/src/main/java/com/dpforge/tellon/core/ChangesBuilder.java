package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.AnnotatedBlock;
import com.dpforge.tellon.core.parser.ParsedSourceCode;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.parser.SourceCodeParser;
import com.dpforge.tellon.core.parser.resolver.AsIsWatcherResolver;
import com.dpforge.tellon.core.parser.resolver.WatcherResolver;

import java.util.*;

public class ChangesBuilder {

    private final SourceCodeParser parser;

    public ChangesBuilder() {
        this(new AsIsWatcherResolver());
    }

    public ChangesBuilder(WatcherResolver watcherResolver) {
        if (watcherResolver == null) {
            throw new NullPointerException("Watcher resolver cannot be null");
        }
        parser = new SourceCodeParser(watcherResolver);
    }

    public Changes build(SourceCode oldSrc, SourceCode newSrc) {
        return buildChanges(parser.parse(oldSrc), parser.parse(newSrc));
    }

    public Changes buildInserted(SourceCode src) {
        final ParsedSourceCode code = parser.parse(src);
        final Changes changes = new Changes();

        for (AnnotatedBlock block : code.getAnnotatedBlocks()) {
            changes.addInserted(block);
        }

        return changes;
    }

    public Changes buildDeleted(SourceCode src) {
        final ParsedSourceCode code = parser.parse(src);
        final Changes changes = new Changes();

        for (AnnotatedBlock block : code.getAnnotatedBlocks()) {
            changes.addDeleted(block);
        }

        return changes;
    }

    private static Changes buildChanges(ParsedSourceCode oldCode, ParsedSourceCode newCode) {
        final Changes changes = new Changes();

        final Map<String, AnnotatedBlock> oldBlocks = new HashMap<>();
        for (AnnotatedBlock block : oldCode.getAnnotatedBlocks()) {
            oldBlocks.put(block.getName(), block);
        }

        for (AnnotatedBlock newBlock : newCode.getAnnotatedBlocks()) {
            final AnnotatedBlock oldBlock = oldBlocks.get(newBlock.getName());
            if (oldBlock != null && oldBlock.getType() == newBlock.getType()) {
                if (!bodyEquals(oldBlock, newBlock)) {
                    changes.addChanged(oldBlock, newBlock);
                }
                oldBlocks.remove(newBlock.getName());
            } else {
                changes.addInserted(newBlock);
            }
        }

        for (AnnotatedBlock oldBlock : oldBlocks.values()) {
            changes.addDeleted(oldBlock);
        }

        return changes;
    }

    private static boolean bodyEquals(final AnnotatedBlock oldBlock, final AnnotatedBlock newBlock) {
        final List<String> oldRaw = oldBlock.getSourceCode().asRaw();
        final List<String> newRaw = newBlock.getSourceCode().asRaw();
        return oldRaw.equals(newRaw);
    }
}
