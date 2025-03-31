package org.xtuml.masl.translate.idm;

import java.util.List;

import org.xtuml.masl.cppgen.ArrayAccess;
import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.VariableDefinitionStatement;
import org.xtuml.masl.translate.main.NlohmannJson;

public class JsonSerializer implements ParameterSerializer {

    private static int id = 0;

    @Override
    public Expression serialize(List<Variable> vars, CodeBlock code) {

        // create output object
        final Variable paramData = new Variable(new TypeUsage(NlohmannJson.json), "data" + (++id));
        if (vars.size() > 0) {
            code.appendStatement(new VariableDefinitionStatement(paramData));
        }

        // set each field
        for (Variable var : vars) {
            final Expression jsonAccess = vars.size() > 1
                    ? new ArrayAccess(paramData.asExpression(), Literal.createStringLiteral(var.getName()))
                    : paramData.asExpression();
            final Expression writeExpr = new BinaryExpression(jsonAccess, BinaryOperator.ASSIGN, var.asExpression());
            code.appendStatement(new ExpressionStatement(writeExpr));
        }

        return NlohmannJson.dump(paramData.asExpression());
    }

    @Override
    public void deserialize(Expression data, List<Variable> vars, CodeBlock code) {

        // parse the JSON object
        final Variable paramJson = new Variable(new TypeUsage(NlohmannJson.json), "_params", NlohmannJson.parse(data));
        if (vars.size() > 0) {
            code.appendStatement(new VariableDefinitionStatement(paramJson));
        }

        // extract each field
        for (Variable var : vars) {
            final Expression paramAccess = NlohmannJson.get(vars.size() > 1
                    ? new ArrayAccess(paramJson.asExpression(), Literal.createStringLiteral(var.getName()))
                    : paramJson.asExpression(), var.getType());
            code.appendStatement(
                    new BinaryExpression(var.asExpression(), BinaryOperator.ASSIGN, paramAccess).asStatement());
        }

    }

}
