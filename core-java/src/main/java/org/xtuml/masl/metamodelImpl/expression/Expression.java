//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.type.AnonymousStructure;
import org.xtuml.masl.metamodelImpl.type.BagType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CharacterType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;
import org.xtuml.masl.metamodelImpl.type.SetType;
import org.xtuml.masl.metamodelImpl.type.StringType;


public abstract class Expression extends Positioned
    implements org.xtuml.masl.metamodel.expression.Expression
{

  Expression ( final Position position )
  {
    super(position);
  }

  @Override
  abstract public BasicType getType ();

  public final List<Expression> getFindArguments ()
  {
    if ( getFindAttributeCount() == 0 )
    {
      return Collections.singletonList(this);
    }
    else
    {
      return getFindArgumentsInner();
    }
  }

  protected List<Expression> getFindArgumentsInner ()
  {
    return Collections.singletonList(this);
  }

  @Override
  public final List<org.xtuml.masl.metamodel.expression.FindParameterExpression> getFindParameters ()
  {
    return Collections.<org.xtuml.masl.metamodel.expression.FindParameterExpression>unmodifiableList(getConcreteFindParameters());
  }

  public final List<FindParameterExpression> getConcreteFindParameters ()
  {
    return getFindParametersInner();
  }

  protected List<FindParameterExpression> getFindParametersInner ()
  {
    return new ArrayList<FindParameterExpression>();
  }


  public int getFindAttributeCount ()
  {
    return 0;
  }

  public final Expression getFindSkeleton ()
  {
    Expression skeleton = null;
    if ( getFindAttributeCount() == 0 )
    {
      skeleton = new FindParameterExpression(getPosition(), getType());
    }
    else
    {
      skeleton = getFindSkeletonInner();
    }
    int paramNo = 1;
    for ( final FindParameterExpression maslParam : skeleton.getConcreteFindParameters() )
    {
      maslParam.setName("p" + paramNo++);
    }

    return skeleton;
  }

  protected Expression getFindSkeletonInner ()
  {
    return this;
  }

  @Override
  public abstract int hashCode ();

  @Override
  public abstract boolean equals ( Object rhs );

  @Override
  public List<AttributeDeclaration> getFindEqualAttributes ()
  {
    return Collections.<AttributeDeclaration>emptyList();
  }

  public final Expression resolve ( final BasicType requiredType )
  {
    return resolve(requiredType, true);
  }

  public final Expression resolve ( final BasicType requiredType, final boolean allowSeqPromote )
  {
    Expression resolved = this;
    if ( allowSeqPromote && requiredType.getPrimitiveType() instanceof CollectionType )
    {
      if ( requiredType.getContainedType().isAssignableFrom(this) )
      {
        try
        {
          final Expression element = resolve(requiredType.getContainedType());
          if ( requiredType.getBasicType() instanceof SequenceType )
          {
            resolved = new CastExpression(new TypeNameExpression(getPosition(), SequenceType.createAnonymous(element.getType())),
                                          element);
          }
          else if ( requiredType.getBasicType() instanceof BagType )
          {
            resolved = new CastExpression(new TypeNameExpression(getPosition(), BagType.createAnonymous(element.getType())),
                                          element);
          }
          else if ( requiredType.getBasicType() instanceof SetType )
          {
            resolved = new CastExpression(new TypeNameExpression(getPosition(), SetType.createAnonymous(element.getType())),
                                          element);
          }
          else if ( requiredType.getBasicType() instanceof StringType )
          {
            if ( element.getType().isAnonymousType() )
            {
              resolved = new CastExpression(new TypeNameExpression(getPosition(), StringType.createAnonymous()), element);
            }
            else if ( element.getType() instanceof CharacterType )
            {
              resolved = new CastExpression(new TypeNameExpression(getPosition(), StringType.create(null, false)), element);
            }
            else
            {
              resolved = new CastExpression(new TypeNameExpression(getPosition(), SequenceType.createAnonymous(element.getType())),
                                            element);
            }
          }
        }
        catch ( final SemanticError e )
        {
          assert false;
        }
      }
    }
    else if ( requiredType.getPrimitiveType() instanceof AnonymousStructure
              && !(getType().getPrimitiveType() instanceof AnonymousStructure) )
    {
      final AnonymousStructure struct = (AnonymousStructure)requiredType.getPrimitiveType();
      if ( struct.getElements().size() == 1 && struct.getElements().get(0).isAssignableFrom(this) )
      {
        resolved = new StructureAggregate(getPosition(), Arrays.<Expression>asList(resolve(struct.getElements().get(0))));
      }
    }
    return resolved.resolveInner(requiredType);
  }

  protected Expression resolveInner ( final BasicType requiredType )
  {
    return this;
  }

  @Override
  public LiteralExpression evaluate ()
  {
    return null;
  }

  public final void checkWriteable ( final Position position ) throws SemanticError
  {
    checkWriteableInner(position);
  }

  public void checkWriteableInner ( final Position position ) throws SemanticError
  {
    throw new SemanticError(SemanticErrorCode.NotWriteable, position);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Collections.<Expression>emptyList();
  }

}
