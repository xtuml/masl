//
// File: FindExpression.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.SetType;



public class FindExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.FindExpression
{

  public static FindExpression create ( final Position position,
                                        final ImplType type,
                                        final Expression collection,
                                        final Expression whereClause )
  {
    if ( collection == null )
    {
      return null;
    }

    try
    {
      return new FindExpression(position, collection, type, whereClause);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  public static ObjectDeclaration getObject ( final Expression expression )
  {
    if ( expression == null )
    {
      return null;
    }

    if ( expression instanceof ObjectNameExpression )
    {
      return ((ObjectNameExpression)expression).getObject();
    }
    else
    {
      final BasicType basicType = expression.getType().getBasicType();
      if ( basicType instanceof CollectionType )
      {
        final CollectionType collType = (CollectionType)basicType;

        if ( collType.getContainedType().getBasicType() instanceof InstanceType )
        {
          return ((InstanceType)collType.getContainedType().getBasicType()).getObjectDeclaration();
        }
      }
    }
    return null;
  }

  private final Expression collection;
  private final Expression condition;
  private final ImplType   findType;

  public enum ImplType
  {
    FIND("find", Type.FIND),
    FIND_ONE("find_one", Type.FIND_ONE),
    FIND_ONLY("find_only", Type.FIND_ONLY);

    private final String name;
    Type                 type;

    private ImplType ( final String name, final Type type )
    {
      this.name = name;
      this.type = type;
    }

    private Type getType ()
    {
      return type;
    }

    @Override
    public String toString ()
    {
      return name;
    }
  }

  
  public FindExpression ( final Position position, final Expression collection, final ImplType findType, final Expression condition ) throws SemanticError
  {
    super(position);
    this.collection = collection;
    this.condition = condition;
    this.findType = findType;

    if ( getObject(collection) == null )
    {
      throw new SemanticError(SemanticErrorCode.FindOnlyOnInstanceCollection, collection.getPosition());
    }
  }

  
  @Override
  public Expression getCollection ()
  {
    return this.collection;
  }

  
  public Expression getCondition ()
  {
    return this.condition;
  }

  @Override
  public Expression getSkeleton ()
  {
    if ( condition != null )
    {
      return condition.getFindSkeleton();
    }
    else
    {
      return null;
    }
  }

  @Override
  public List<Expression> getArguments ()
  {
    return Collections.unmodifiableList(condition.getFindArguments());
  }

  
  @Override
  public Type getFindType ()
  {
    return this.findType.getType();
  }

  /**
   * 
   * @return
   * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
   */
  @Override
  public BasicType getType ()
  {
    if ( findType == ImplType.FIND_ONE || findType == ImplType.FIND_ONLY )
    {
      return getInstanceType();
    }
    else if ( collection instanceof ObjectNameExpression )
    {
      return SetType.createAnonymous(getInstanceType());
    }
    else
    {
      return collection.getType();
    }
  }

  @Override
  public InstanceType getInstanceType ()
  {
    if ( collection instanceof ObjectNameExpression )
    {
      return ((ObjectNameExpression)collection).getObject().getType();
    }
    else
    {
      return (InstanceType)((CollectionType)collection.getType()).getContainedType();
    }

  }

  @Override
  public String toString ()
  {
    return "" + findType + " " + collection + " (" + (condition == null ? "" : condition.toString()) + ")";

  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof FindExpression) )
    {
      return false;
    }
    else
    {
      final FindExpression obj2 = (FindExpression)obj;

      return collection.equals(obj2.collection) && findType.equals(obj2.findType) && condition == null ? obj2.condition == null
                                                                                                      : condition
                                                                                                                 .equals(obj2.condition);
    }
  }

  @Override
  public int hashCode ()
  {

    return collection.hashCode() ^ (condition == null ? 0 : condition.hashCode()) ^ findType.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitFindExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.asList(collection, condition);
  }


}
