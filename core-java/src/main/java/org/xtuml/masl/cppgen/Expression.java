/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Abstract class which is the superclass of all expressions.
 */
public abstract class Expression {

    private final Set<CodeFile> dependentHeaders = new HashSet<CodeFile>();

    /**
     * Calculates the precedence of the expression. The precedence is used when
     * determining how to parenthesise a compound expression to maintain
     * semantics. Precedence is determined according to the table in C++
     * Programming Language (Third Edition) Stroustrup, Section 6.2, sumarised
     * below. Expressions which do not appear in the original table, eg constants
     * and variable references should be allocated precedence 0.
     *
     * <p>
     * <table border=1 cellspacing=0 align=center width=75%>
     * <tr>
     * <th>precedence</td>
     * <th>operator</th>
     * </tr>
     * <tr>
     * <tr>
     * <td align=center>0</td>
     * <td>literals, variable references, function calls</td>
     * </tr>
     * <tr>
     * <td align=center>1</td>
     * <td>::</td>
     * </tr>
     * <tr>
     * <td align=center>2</td>
     * <td>{@literal o.m, p->m, p[e], f(), T(), e++, e--, typeid, xxx_cast<>}</td>
     * </tr>
     * <tr>
     * <td align=center>3</td>
     * <td>sizeof, ++e, --e, ~e, !e, -e, +e, &e, *e, new, delete, (T)e</td>
     * </tr>
     * <tr>
     * <td align=center>4</td>
     * <td>{@literal o.*pm, p->*pm}</td>
     * </tr>
     * <tr>
     * <td align=center>5</td>
     * <td>e*e, e/e, e%e</td>
     * </tr>
     * <tr>
     * <td align=center>6</td>
     * <td>e+e, e-e</td>
     * </tr>
     * <tr>
     * <td align=center>7</td>
     * <td>{@literal e<<e, e>>e}</td>
     * </tr>
     * <tr>
     * <td align=center>8</td>
     * <td>{@literal e<e, e<=e, e>e, e>=e}</td>
     * </tr>
     * <tr>
     * <td align=center>9</td>
     * <td>e==e, e!=e</td>
     * </tr>
     * <tr>
     * <td align=center>10</td>
     * <td>e&e</td>
     * </tr>
     * <tr>
     * <td align=center>11</td>
     * <td>e^e</td>
     * </tr>
     * <tr>
     * <td align=center>12</td>
     * <td>e|e</td>
     * </tr>
     * <tr>
     * <td align=center>13</td>
     * <td>e&&e</td>
     * </tr>
     * <tr>
     * <td align=center>14</td>
     * <td>e||e</td>
     * </tr>
     * <tr>
     * <td align=center>15</td>
     * <td>e?e:e</td>
     * </tr>
     * <tr>
     * <td align=center>16</td>
     * <td>{@literal e=e,e*=e,e/=e,e%=e,e+=e,e-=e,e<<=e,e>>=e,e&=e,e|=e,e^=e}</td>
     * </tr>
     * <tr>
     * <td align=center>17</td>
     * <td>throw</td>
     * </tr>
     * <tr>
     * <td align=center>18</td>
     * <td>e,e</td>
     * </tr>
     * </table>
     * </p>
     *
     * @return the precedence of the expression.
     */
    abstract int getPrecedence();

    Expression() {
    }

    @Override
    public String toString() {
        return getCode(null, "");
    }

    /**
     * Returns the code representing the expression. Expression#getPrecedence() is
     * used to determine the correct parenthesisation of the expression. An
     * alignment may be specified to allow correct indentation of the code.
     * <p>
     * <p>
     * The namespace that the expression is in. This is used to determine
     * how much scope information is required to uniquely resolve any
     * names used in the expression.
     * <p>
     * any tabs or leading space needed for aesthetics
     *
     * @return the code for the expression
     */
    abstract String getCode(Namespace currentNamespace, String alignment);

    /**
     * Returns code representing the expression.
     * <p>
     * <p>
     * The namespace that the expression is in. This is used to determine
     * how much scope information is required to uniquely resolve any
     * names used in the expression.
     *
     * @return the code for the expression
     */
    String getCode(final Namespace currentNamespace) {
        return getCode(currentNamespace, "");
    }

    /**
     * Calculates the set of forward declarations needed to allow this expression
     * to compile.
     *
     * @return forward declarations required
     */
    Set<Declaration> getForwardDeclarations() {
        return new LinkedHashSet<Declaration>();
    }

    /**
     * When creating expressions involving templates, additional header files
     * might be required to resolve the types required by the template
     * instatiation mechansim. Therefore all additional header files to be
     * associated with the expression
     * <p>
     * <p>
     * The header that the expression depends upon
     */
    public void addInclude(final CodeFile header) {
        dependentHeaders.add(header);
    }

    /**
     * Calculates the set of include files needed to allow this expression to
     * compile.
     *
     * @return include files required
     */
    Set<CodeFile> getIncludes() {
        return dependentHeaders;
    }

    /**
     * Calculates whether the type of the expression is a class template. This is
     * needed when calling a function or accessing a member of the class which is
     * also a template, because c++ requires the xxx.template function() syntax.
     *
     * @return whether the type is a class template.
     */
    boolean isTemplateType() {
        return false;
    }

    public Statement asStatement() {
        return new ExpressionStatement(this);
    }

}
