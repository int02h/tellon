package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.AnnotatedBlock;
import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.parser.SourceCodeParser;

import java.util.HashMap;
import java.util.Map;

public class DiffBuilder {
    public Diff build(String oldSrc, String newSrc) {
        final SourceCode oldCode = SourceCodeParser.parse(oldSrc);
        final SourceCode newCode = SourceCodeParser.parse(newSrc);
        return build(oldCode, newCode);
    }

    private static Diff build(SourceCode oldCode, SourceCode newCode) {
        final Diff diff = new Diff();

        final Map<String, AnnotatedBlock> oldBlocks = new HashMap<>();
        for (AnnotatedBlock block : oldCode.getAnnotatedBlocks()) {
            oldBlocks.put(block.getDescription(), block);
        }

        for (AnnotatedBlock newBlock : newCode.getAnnotatedBlocks()) {
            final AnnotatedBlock oldBlock = oldBlocks.get(newBlock.getDescription());
            if (oldBlock != null && oldBlock.getType() == newBlock.getType()) {
                if (!oldBlock.getBody().equals(newBlock.getBody())) {
                    diff.addChanged(oldBlock, newBlock);
                }
                oldBlocks.remove(newBlock.getDescription());
            } else {
                diff.addInserted(newBlock);
            }
        }

        for (AnnotatedBlock oldBlock : oldBlocks.values()) {
            diff.addDeleted(oldBlock);
        }

        return diff;
    }
}
