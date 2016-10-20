//
// File: ReturnStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.DictionaryAccessExpression;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public class EraseStatement extends Statement
    implements org.xtuml.masl.metamodel.code.EraseStatement
{

  private final Expression dictionary;
  private final Expression key;

  public static EraseStatement create ( final Position position, final Expression expression )
  {
    if ( expression == null )
    {
      return null;
    }

    try
    {
      if ( expression instanceof DictionaryAccessExpression )
      {
        return new EraseStatement(position,
                                  ((DictionaryAccessExpression)expression).getPrefix(),
                                  ((DictionaryAccessExpression)expression).getKey());
      }
      else
      {
        throw new SemanticError(SemanticErrorCode.EraseOnlyValidforDictionary, position);
      }

    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  private EraseStatement ( final Position position, final Expression dictionary, final Expression key )
  {
    super(position);

    this.dictionary = dictionary;
    this.key = key;
  }

  
  @Override
  public Expression getDictionary ()
  {
    return dictionary;
  }

  
  @Override
  public Expression getKey ()
  {
    return key;
  }

  @Override
  public String toString ()
  {
    return "erase " + dictionary + "[" + key + "];";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEraseStatement(this, p);
  }


}
