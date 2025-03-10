package org.xtuml.masl.translate.kafka;

import java.util.List;

import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Variable;

interface ParameterSerializer {

    Expression serialize(List<Variable> vars, CodeBlock code);

    void deserialize(Expression data, List<Variable> vars, CodeBlock code);

}
