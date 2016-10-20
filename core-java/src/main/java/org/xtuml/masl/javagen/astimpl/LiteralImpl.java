//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.Literal;


abstract class LiteralImpl extends ExpressionImpl
    implements Literal
{

  static class BooleanLiteralImpl extends LiteralImpl
      implements BooleanLiteral
  {

    BooleanLiteralImpl ( final ASTImpl ast, final boolean value )
    {
      super(ast);
      setValue(value);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitBooleanLiteral(this, p);
    }

    @Override
    public boolean getValue ()
    {
      return value;
    }

    @Override
    public void setValue ( final boolean value )
    {
      this.value = value;
    }

    private boolean value;
  }

  static class CharacterLiteralImpl extends LiteralImpl
      implements CharacterLiteral
  {

    CharacterLiteralImpl ( final ASTImpl ast, final char value )
    {
      super(ast);
      setValue(value);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitCharacterLiteral(this, p);
    }

    @Override
    public char getValue ()
    {
      return value;
    }

    @Override
    public void setValue ( final char value )
    {
      this.value = value;
    }

    private char value;
  }


  static class DoubleLiteralImpl extends LiteralImpl
      implements DoubleLiteral
  {

    DoubleLiteralImpl ( final ASTImpl ast, final double value )
    {
      super(ast);
      setValue(value);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitDoubleLiteral(this, p);
    }

    @Override
    public double getValue ()
    {
      return value;
    }

    @Override
    public void setValue ( final double value )
    {
      this.value = value;
    }

    private double value;
  }

  static class FloatLiteralImpl extends LiteralImpl
      implements FloatLiteral
  {

    FloatLiteralImpl ( final ASTImpl ast, final float value )
    {
      super(ast);
      setValue(value);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitFloatLiteral(this, p);
    }

    @Override
    public float getValue ()
    {
      return value;
    }

    @Override
    public void setValue ( final float value )
    {
      this.value = value;
    }

    private float value;
  }


  static class IntegerLiteralImpl extends LiteralImpl
      implements IntegerLiteral
  {

    IntegerLiteralImpl ( final ASTImpl ast, final int value )
    {
      super(ast);
      setValue(value);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitIntegerLiteral(this, p);
    }

    @Override
    public int getValue ()
    {
      return value;
    }

    @Override
    public void setValue ( final int value )
    {
      this.value = value;
    }

    private int value;
  }

  static class LongLiteralImpl extends LiteralImpl
      implements LongLiteral
  {

    LongLiteralImpl ( final ASTImpl ast, final long value )
    {
      super(ast);
      setValue(value);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitLongLiteral(this, p);
    }

    @Override
    public long getValue ()
    {
      return value;
    }

    @Override
    public void setValue ( final long value )
    {
      this.value = value;
    }

    private long value;
  }

  static class NullLiteralImpl extends LiteralImpl
      implements NullLiteral
  {

    NullLiteralImpl ( final ASTImpl ast )
    {
      super(ast);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitNullLiteral(this, p);
    }

  }


  static class StringLiteralImpl extends LiteralImpl
      implements StringLiteral
  {

    StringLiteralImpl ( final ASTImpl ast, final String value )
    {
      super(ast);
      setValue(value);
    }

    @Override
    public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
    {
      return v.visitStringLiteral(this, p);
    }

    @Override
    public String getValue ()
    {
      return value;
    }

    @Override
    public void setValue ( final String value )
    {
      this.value = value;
    }

    private String value;
  }


  LiteralImpl ( final ASTImpl ast )
  {
    super(ast);

  }


  @Override
  protected int getPrecedence ()
  {
    return Integer.MAX_VALUE;
  }

}
