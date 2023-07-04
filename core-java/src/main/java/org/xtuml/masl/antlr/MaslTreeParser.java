/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.antlr;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.utils.TextUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MaslTreeParser extends TreeParser {

    public MaslTreeParser(final TreeNodeStream input, final RecognizerSharedState state) {
        super(input, state);
    }

    protected java.io.File currentFile = null;

    public MaslTreeParser(final File file) throws IOException, RecognitionException {
        super(null);

        this.currentFile = file;
        fileReader = new MaslFileReader(file);

        final ANTLRReaderStream
                input =
                new ANTLRReaderStream(new BufferedReader(new InputStreamReader(new FileInputStream(file),
                                                                               StandardCharsets.ISO_8859_1)));

        final MaslPLexer lexer = new MaslPLexer(input);
        lexer.setFileReader(fileReader);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final MaslPParser parser = new MaslPParser(tokens);
        parser.setFileReader(fileReader);

        final CommonTree tree = (CommonTree) parser.target().getTree();

        final CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
        nodes.setTokenStream(tokens);

        setTreeNodeStream(nodes);

    }

    @Override
    public String getErrorHeader(final RecognitionException e) {
        return "Node from " +
               (e.approximateLineInfo ? "after " : "") +
               currentFile.getName() +
               ":" +
               e.line +
               ":" +
               e.charPositionInLine;
    }

    @Override
    public void reportError(final RecognitionException e) {
        if (e instanceof MismatchedTreeNodeException &&
            e.node instanceof CommonErrorNode) {
            return;
        }
        super.reportError(e);
    }

    private class FilePosition extends Position {

        FilePosition(final int line, final int charPos) {
            this.line = line;
            this.charPos = charPos;
        }

        @Override
        public String getText() {
            return currentFile.getName() + ":" + line + ":" + charPos;
        }

        @Override
        public String getContext() {
            if (line == 0) {
                return "";
            }
            return fileReader.getFileLine(line) + "\n" + TextUtils.filledString('.', charPos) + "^";
        }

        private final int line;
        private final int charPos;

        @Override
        public int getLineNumber() {
            return line;
        }
    }

    Position getPosition(final CommonTree... nodes) {
        for (final CommonTree node : nodes) {
            if (node != null) {
                return new FilePosition(node.getLine(), node.getCharPositionInLine());
            }
        }
        return null;
    }

    Position getPosition(final String text) {
        return Position.getPosition(text);
    }

    void registerPosition(final CommonTree node) {
        // Force token to cache it's text (rather than extacting it form the file
        // each time) so that the position lookups are using the same String object
        node.getToken().setText(node.getToken().getText());

        Position.registerPosition(node.getText(), getPosition(node));
    }

    private MaslFileReader fileReader;

}
