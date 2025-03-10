/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A C++ try-catch statement
 */
public class TryCatchBlock extends Statement {

    /**
     * The catch
     */
    public static class CatchBlock {

        public CatchBlock(final Variable exception, final CodeBlock catchBlock) {
            this.exception = exception;
            this.catchBlock = catchBlock;
        }

        Set<Declaration> getForwardDeclarations() {
            final Set<Declaration> result = catchBlock.getForwardDeclarations();
            return result;
        }

        Set<CodeFile> getIncludes() {
            final Set<CodeFile> result = catchBlock.getIncludes();
            if (exception != null) {
                // Need full declaration of exception, even if caught by reference
                result.addAll(exception.getType().getNoRefDirectUsageIncludes());
            }
            return result;
        }

        void setParent(final Statement parent) {
            catchBlock.setParent(parent);
        }

        void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
            if (exception == null) {
                writer.write("\n" + indent + "catch ( ... )\n");
            } else {
                writer.write("\n" + indent + "catch ( " + exception.getParameterDefinition(currentNamespace) + " )\n");
            }
            catchBlock.write(writer, indent, currentNamespace);
        }

        private final Variable exception;

        private final CodeBlock catchBlock;

    }

    public TryCatchBlock(final CodeBlock tryBlock, final CatchBlock... catchBlocks) {
        this(tryBlock, Arrays.asList(catchBlocks));
    }

    public TryCatchBlock(final CodeBlock tryBlock, final List<CatchBlock> catchBlocks) {
        tryBlock.setParent(this);
        for (final CatchBlock catchBlock : catchBlocks) {
            catchBlock.setParent(this);
        }

        this.tryBlock = tryBlock;
        this.catchBlocks = catchBlocks;
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = super.getForwardDeclarations();
        result.addAll(tryBlock.getForwardDeclarations());
        for (final CatchBlock catchBlock : catchBlocks) {
            result.addAll(catchBlock.getForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = super.getIncludes();
        result.addAll(tryBlock.getIncludes());
        for (final CatchBlock catchBlock : catchBlocks) {
            result.addAll(catchBlock.getIncludes());
        }
        return result;
    }

    @Override
    /**
     *
     * @throws IOException
     * @see org.xtuml.masl.cppgen.Statement#write(java.io.Writer, java.lang.String)
     */
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        writer.write(indent + "try\n");
        tryBlock.write(writer, indent, currentNamespace);
        for (final CatchBlock catchBlock : catchBlocks) {
            catchBlock.write(writer, indent, currentNamespace);
        }
    }

    private final CodeBlock tryBlock;

    private final List<CatchBlock> catchBlocks;

}
