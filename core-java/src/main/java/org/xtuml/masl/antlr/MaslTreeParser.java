//
// File: MaslTreeParser.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.antlr;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.MismatchedTreeNodeException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.CommonErrorNode;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.utils.TextUtils;


public class MaslTreeParser extends TreeParser
{

  public MaslTreeParser ( final TreeNodeStream input, final RecognizerSharedState state )
  {
    super(input, state);
  }

  protected java.io.File currentFile = null;

  public MaslTreeParser ( final File file ) throws IOException, RecognitionException
  {
    super(null);

    this.currentFile = file;
    fileReader = new MaslFileReader(file);

    final ANTLRReaderStream input = new ANTLRReaderStream(new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1)));

    final MaslPLexer lexer = new MaslPLexer(input);
    lexer.setFileReader(fileReader);
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final MaslPParser parser = new MaslPParser(tokens);
    parser.setFileReader(fileReader);

    final CommonTree tree = (CommonTree)parser.target().getTree();

    final CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
    nodes.setTokenStream(tokens);

    setTreeNodeStream(nodes);

  }

  @Override
  public String getErrorHeader ( final RecognitionException e )
  {
    return "Node from " +
           (e.approximateLineInfo ? "after " : "") + currentFile.getName() + ":" + e.line + ":" + e.charPositionInLine;
  }

  @Override
  public void reportError ( final RecognitionException e )
  {
    if ( e instanceof MismatchedTreeNodeException && ((MismatchedTreeNodeException)e).node instanceof CommonErrorNode )
    {
      return;
    }
    super.reportError(e);
  }


  private class FilePosition extends Position
  {

    FilePosition ( final int line, final int charPos )
    {
      this.line = line;
      this.charPos = charPos;
    }

    @Override
    public String getText ()
    {
      return currentFile.getName() + ":" + line + ":" + charPos;
    }

    @Override
    public String getContext ()
    {
      if ( line == 0 )
      {
        return "";
      }
      return fileReader.getFileLine(line) + "\n" + TextUtils.filledString('.', charPos) + "^";
    }

    private final int line;
    private final int charPos;

    @Override
    public int getLineNumber ()
    {
      return line;
    }
  }

  Position getPosition ( final CommonTree... nodes )
  {
    for ( final CommonTree node : nodes )
    {
      if ( node != null )
      {
        return new FilePosition(node.getLine(), node.getCharPositionInLine());
      }
    }
    return null;
  }

  Position getPosition ( final String text )
  {
    return Position.getPosition(text);
  }

  void registerPosition ( final CommonTree node )
  {
    // Force token to cache it's text (rather than extacting it form the file
    // each time) so that the position lookups are using the same String object
    node.getToken().setText(node.getToken().getText());

    Position.registerPosition(node.getText(), getPosition(node));
  }

  private MaslFileReader fileReader;

}
