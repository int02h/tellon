package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.AnnotatedBlock;
import com.dpforge.tellon.core.parser.ParsedSourceCode;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.parser.SourceCodeParser;

import java.util.HashMap;
import java.util.Map;

public class ChangesBuilder {
    public Changes build(SourceCode oldSrc, SourceCode newSrc) {
        final SourceCodeParser parser = new SourceCodeParser();
        return buildChanges(parser.parse(oldSrc), parser.parse(newSrc));
    }

    public Changes buildInserted(SourceCode src) {
        final ParsedSourceCode code = new SourceCodeParser().parse(src);
        final Changes changes = new Changes();

        for (AnnotatedBlock block : code.getAnnotatedBlocks()) {
            changes.addInserted(block);
        }

        return changes;
    }

    public Changes buildDeleted(SourceCode src) {
        final ParsedSourceCode code = new SourceCodeParser().parse(src);
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
                if (!oldBlock.getBody().equals(newBlock.getBody())) {
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
}
