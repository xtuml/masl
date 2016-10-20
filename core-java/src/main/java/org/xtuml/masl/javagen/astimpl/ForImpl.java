//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.For;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.code.Statement;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.StatementExpression;


public class ForImpl extends StatementImpl
    implements For
{

  public ForImpl ( final ASTImpl ast )
  {
    super(ast);
    setStatement(ast.createCodeBlock());
  }

  public ForImpl ( final ASTImpl ast,
                       final StatementExpression start,
                       final Expression condition,
                       final StatementExpression update )
  {
    super(ast);
    addStartExpression(start);
    setCondition(condition);
    addUpdateExpression(update);
    setStatement(ast.createCodeBlock());
  }

  public ForImpl ( final ASTImpl ast, final LocalVariable variable, final Expression condition, final StatementExpression update )
  {
    super(ast);
    setVariable(variable);
    setCondition(condition);
    addUpdateExpression(update);
    setStatement(ast.createCodeBlock());
  }

  public ForImpl ( final ASTImpl ast, final LocalVariable variable, final Expression collection )
  {
    super(ast);
    setVariable(variable);
    setCollection(collection);
    setStatement(ast.createCodeBlock());
  }


  @Override
  public void addStartExpression ( final StatementExpression expression )
  {
    starts.add((ExpressionImpl)expression);
  }

  @Override
  public void addUpdateExpression ( final StatementExpression expression )
  {
    updates.add((ExpressionImpl)expression);
  }

  @Override
  public Expression getCondition ()
  {
    return condition.get();
  }

  @Override
  public Expression getCollection ()
  {
    return collection.get();
  }

  @Override
  public List<? extends StatementExpression> getStartExpressions ()
  {
    return starts;
  }

  @Override
  public LocalVariable getVariable ()
  {
    return variable.get();
  }

  @Override
  public Statement getStatement ()
  {
    return code.get();
  }

  @Override
  public List<? extends StatementExpression> getUpdateExpressions ()
  {
    return updates;
  }

  @Override
  public void setCollection ( final Expression collection )
  {
    this.collection.set((ExpressionImpl)collection);
  }


  @Override
  public void setCondition ( final Expression condition )
  {
    this.condition.set((ExpressionImpl)condition);
  }

  @Override
  public void setVariable ( final LocalVariable variable )
  {
    this.variable.set((LocalVariableImpl)variable);
  }

  @Override
  public void setStatement ( final Statement statement )
  {
    this.code.set((StatementImpl)statement);
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitFor(this, p);
  }

  private final ChildNode<StatementImpl>      code       = new ChildNode<StatementImpl>(this);
  private final ChildNode<ExpressionImpl>     condition  = new ChildNode<ExpressionImpl>(this);
  private final ChildNode<LocalVariableImpl>  variable   = new ChildNode<LocalVariableImpl>(this);
  private final ChildNode<ExpressionImpl>     collection = new ChildNode<ExpressionImpl>(this);
  private final ChildNodeList<ExpressionImpl> updates    = new ChildNodeList<ExpressionImpl>(this);
  private final ChildNodeList<ExpressionImpl> starts     = new ChildNodeList<ExpressionImpl>(this);

}
