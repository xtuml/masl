package org.xtuml.masl.translate.kafka;

import java.util.List;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Std;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;

public class BufferedIOSerializer implements ParameterSerializer {

    @Override
    public Expression serialize(List<Variable> vars, CodeBlock code) {
        // create the buffered IO object
        final Variable buf = new Variable(new TypeUsage(Kafka.bufferedOutputStream), "buf");
        if (vars.size() > 0) {
            code.appendStatement(new VariableDefinitionStatement(buf));
        }

        // write each field
        for (Variable var : vars) {
            code.appendStatement(new BinaryExpression(buf.asExpression(), BinaryOperator.LEFT_SHIFT, var.asExpression())
                    .asStatement());
        }

        return Std.vector(new TypeUsage(Std.uint8)).callConstructor(
                new Function("begin").asFunctionCall(buf.asExpression(), false),
                new Function("end").asFunctionCall(buf.asExpression(), false));
    }

    @Override
    public void deserialize(Expression data, List<Variable> vars, CodeBlock code) {
        // create the buffered IO object
        final Variable buf = new Variable(new TypeUsage(Kafka.bufferedInputStream), "buf",
                Kafka.bufferedInputStream.callConstructor(new Function("begin").asFunctionCall(data, false),
                        new Function("end").asFunctionCall(data, false)));
        if (vars.size() > 0) {
            code.appendStatement(new VariableDefinitionStatement(buf));
        }

        // extract each field
        for (Variable var : vars) {
            code.appendStatement(
                    new BinaryExpression(buf.asExpression(), BinaryOperator.RIGHT_SHIFT, var.asExpression())
                            .asStatement());
        }

    }

}
