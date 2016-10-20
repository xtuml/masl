//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import java.util.List;

import org.xtuml.masl.javagen.ast.def.TypeBody;
import org.xtuml.masl.javagen.ast.types.DeclaredType;
import org.xtuml.masl.javagen.ast.types.ReferenceType;


public interface NewInstance
    extends StatementExpression
{

  Expression addArgument ( Expression arg );

  ReferenceType addTypeArgument ( ReferenceType typeArg );

  List<? extends Expression> getArguments ();

  DeclaredType getInstanceType ();

  List<? extends ReferenceType> getTypeArguments ();

  DeclaredType setInstanceType ( DeclaredType constructor );

  TypeBody setTypeBody ( TypeBody body );

  TypeBody setTypeBody ();

  TypeBody getTypeBody ();

  Expression getOuterInstance ();

  Expression setOuterInstance ( Expression instance );

}
