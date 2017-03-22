package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.AnnotatedBlock;
import com.dpforge.tellon.core.parser.ParsedSourceCode;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.parser.SourceCodeParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChangesBuilder {
    public Changes build(String oldSrc, String newSrc) {
        final SourceCodeParser parser = new SourceCodeParser();
        return build(parser.parse(oldSrc), parser.parse(newSrc));
    }

    public Changes build(SourceCode oldSrc, SourceCode newSrc) throws IOException {
        final SourceCodeParser parser = new SourceCodeParser();
        return build(parser.parse(oldSrc), parser.parse(newSrc));
    }

    private static Changes build(ParsedSourceCode oldCode, ParsedSourceCode newCode) {
        final Changes changes = new Changes();

        final Map<String, AnnotatedBlock> oldBlocks = new HashMap<>();
        for (AnnotatedBlock block : oldCode.getAnnotatedBlocks()) {
            oldBlocks.put(block.getDescription(), block);
        }

        for (AnnotatedBlock newBlock : newCode.getAnnotatedBlocks()) {
            final AnnotatedBlock oldBlock = oldBlocks.get(newBlock.getDescription());
            if (oldBlock != null && oldBlock.getType() == newBlock.getType()) {
                if (!oldBlock.getBody().equals(newBlock.getBody())) {
                    changes.addChanged(oldBlock, newBlock);
                }
                oldBlocks.remove(newBlock.getDescription());
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
