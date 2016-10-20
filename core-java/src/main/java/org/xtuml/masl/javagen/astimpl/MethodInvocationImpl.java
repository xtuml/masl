//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Method;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.MethodInvocation;
import org.xtuml.masl.javagen.ast.types.ReferenceType;


class MethodInvocationImpl extends ExpressionImpl
    implements MethodInvocation
{

  MethodInvocationImpl ( final ASTImpl ast, final Expression instance, final Method method, final Expression... args )
  {
    super(ast);
    setInstance(instance);
    setMethod(method);
    for ( final Expression arg : args )
    {
      addArgument(arg);
    }
  }

  MethodInvocationImpl ( final ASTImpl ast, final Method method, final Expression... args )
  {
    super(ast);
    setMethod(method);
    for ( final Expression arg : args )
    {
      addArgument(arg);
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitMethodInvocation(this, p);
  }


  @Override
  public ExpressionImpl addArgument ( final Expression argument )
  {
    this.arguments.add((ExpressionImpl)argument);
    return (ExpressionImpl)argument;
  }

  @Override
  public ReferenceTypeImpl addTypeArgument ( final ReferenceType argument )
  {
    typeArguments.add((ReferenceTypeImpl)argument);
    return (ReferenceTypeImpl)argument;
  }


  @Override
  public void forceQualifier ()
  {
    if ( qualifier.get() == null && instance.get() == null )
    {
      if ( method.getModifiers().isStatic() )
      {
        setQualifier(new TypeQualifierImpl(getAST(), method.getDeclaringType()));
      }
      else if ( isSuper )
      {
        setQualifier(new SuperQualifierImpl(getAST(), getApparentType()));
      }
      else
      {
        setInstance(new ThisImpl(getAST(), getApparentType()));
      }
    }
  }

  @Override
  public List<? extends ExpressionImpl> getArguments ()
  {
    return Collections.unmodifiableList(arguments);
  }

  @Override
  public ExpressionImpl getInstance ()
  {
    if ( qualifier.get() == null && instance.get() == null && (isSuper || getEnclosingScope().requiresQualifier(this)) )
    {
      forceQualifier();
    }
    return instance.get();
  }

  @Override
  public MethodImpl getMethod ()
  {
    return method;
  }

  @Override
  public QualifierImpl getQualifier ()
  {
    if ( qualifier.get() == null && instance.get() == null && (isSuper || getEnclosingScope().requiresQualifier(this)) )
    {
      forceQualifier();
    }

    return qualifier.get();
  }

  @Override
  public List<ReferenceTypeImpl> getTypeArguments ()
  {
    return Collections.unmodifiableList(typeArguments);
  }

  @Override
  public boolean isSuper ()
  {
    return isSuper;
  }


  @Override
  public Expression setInstance ( Expression instance )
  {
    if ( ((ExpressionImpl)instance).getPrecedence() < getPrecedence() )
    {
      instance = getAST().createParenthesizedExpression(instance);
    }
    this.instance.set((ExpressionImpl)instance);
    return instance;
  }

  @Override
  public Method setMethod ( final Method method )
  {
    this.method = (MethodImpl)method;
    return method;
  }

  @Override
  public void setSuper ()
  {
    isSuper = true;
  }

  @Override
  protected int getPrecedence ()
  {
    return 15;
  }

  private TypeBodyImpl getApparentType ()
  {
    // Method called on 'this' or 'super', but need to know which this we are
    // talking about... could be from an enclosing class.
    TypeBodyImpl enclosingType = getEnclosingTypeBody();
    while ( enclosingType != null )
    {
      TypeBodyImpl superType = enclosingType;
      while ( superType != null )
      {
        if ( superType == method.getEnclosingTypeBody() )
        {
          // We've found the method on a superclass
          return enclosingType;
        }
        superType = superType.getSupertype() == null ? null : superType.getSupertype().getTypeBody();
      }
      // Not found on this type, so try the enclosing type
      enclosingType = enclosingType.getEnclosingTypeBody();
    }
    return null;
  }

  private void setQualifier ( final QualifierImpl qualifier )
  {
    this.qualifier.set(qualifier);
  }

  private final ChildNodeList<ExpressionImpl> arguments     = new ChildNodeList<ExpressionImpl>(this);

  private MethodImpl                          method;

  private final ChildNode<ExpressionImpl>     instance      = new ChildNode<ExpressionImpl>(this);

  private final ChildNode<QualifierImpl>      qualifier     = new ChildNode<QualifierImpl>(this);

  private final List<ReferenceTypeImpl>       typeArguments = new ChildNodeList<ReferenceTypeImpl>(this);

  private boolean                             isSuper       = false;


}
