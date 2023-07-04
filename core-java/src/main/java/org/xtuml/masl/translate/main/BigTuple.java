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
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.TypeUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * A tuple structure with more elements than Boost.tuple can cope with.
 * Boost.tuple has a maximum number of elements per tuple (currently 10) that we
 * may need to exceed. We therefore create a BigTuple capable of nesting Boost
 * tuples to whatever depth is required to allow the number of elements
 * supplied.
 */
public final class BigTuple {

    /**
     * Returns a call to boost::make_tuple, with nested calls where necessary to
     * conform to the structure of a corresponding BigTuple with the same number of
     * elements.
     * <p>
     * <p>
     * A list of parameters used to construct the tuple
     *
     * @return an Expression representing a call to boost::make_tuple
     */
    public static Expression getMakeTuple(final List<Expression> params) {
        final List<Expression> tupleparams = new ArrayList<>();

        if (params.size() > Boost.MAX_TUPLE_SIZE) {
            for (int i = 0; i < params.size(); i += Boost.MAX_TUPLE_SIZE) {
                tupleparams.add(getMakeTuple(params.subList(i, Math.min(i + Boost.MAX_TUPLE_SIZE, params.size()))));
            }
            return getMakeTuple(tupleparams);
        } else {
            return Boost.makeTuple.asFunctionCall(params);
        }
    }

    /**
     * Returns a call to boost::tuple constructor, with nested calls where necessary
     * to conform to the structure of a corresponding BigTuple with the same number
     * of elements.
     * <p>
     * <p>
     * A list of parameters used to construct the tuple
     *
     * @return an Expression representing a call to the tuple constructor
     */
    public Expression callConstructor(final List<Expression> params) {
        final List<Expression> tupleparams = new ArrayList<>();

        if (params.size() > Boost.MAX_TUPLE_SIZE) {
            for (int i = 0; i < params.size(); i += Boost.MAX_TUPLE_SIZE) {
                tupleparams.add(childTuples.get(i / Boost.MAX_TUPLE_SIZE).callConstructor(params.subList(i,
                                                                                                         Math.min(i +
                                                                                                                  Boost.MAX_TUPLE_SIZE,
                                                                                                                  params.size()))));
            }
            return getMakeTuple(tupleparams);
        } else {
            return tupleType.getType().callConstructor(params);
        }
    }

    /**
     * Returns a list of the calls to boost::make_tuple required to handle the
     * number of supplied parameters. As boost::make_tuple cannot have more than 10
     * parameters, a list of parameters exceeding this count will cause multiple
     * boost::make_tuple expressions to be returned in the list.
     * <p>
     * <p>
     * A list of parameters used to construct the tuple
     *
     * @return a list of boost::make_tuple expressions
     */

    public static List<Expression> getTupleList(final List<Expression> params) {
        final List<Expression> tupleList = new ArrayList<>();
        if (params.size() > Boost.MAX_TUPLE_SIZE) {
            List<Expression> currentParams = new ArrayList<>();
            for (int i = 1; i <= params.size(); ++i) {
                currentParams.add(params.get(i - 1));
                if (i % Boost.MAX_TUPLE_SIZE == 0) {
                    tupleList.add(Boost.makeTuple.asFunctionCall(currentParams));
                    currentParams = new ArrayList<>();
                }
            }

            if (currentParams.size() > 0) {
                tupleList.add(Boost.makeTuple.asFunctionCall(currentParams));
            }
        } else {
            tupleList.add(Boost.makeTuple.asFunctionCall(params));
        }
        return tupleList;
    }

    /**
     * Create a BigTuple with elements of the supplied types. The resultant
     * structure will be a Boost tuple with nested tuples where necessary to keep
     * the maximum tuple site down to the maximum tuple size. If a Boost.tuple could
     * cope with the supplied numnber of elements, then a raw Boost.tuple is used.
     * <p>
     * For example, given a maximum tuple size of 10, a tuple with 15 elements
     * (T1,T2,T3...,T15) will consist of a tuple with two nested tuple elements. The
     * first of these nested tuples will be a tuple of 10 elements, corresponding to
     * T1-T10. The second will be a tuple of 5 elements, corresponding to T11-T15.
     * {@code
     * boost::tuple<boost::tuple<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>,boost::tuple<T11,
     * T12,T13,T14,T15> >}
     * <p>
     * <p>
     * The list of types for the tuple elements
     */
    public BigTuple(final List<TypeUsage> types) {
        if (types.size() > Boost.MAX_TUPLE_SIZE) {
            int noChildTuples = types.size();
            childTupleSize = 1;
            while (noChildTuples > Boost.MAX_TUPLE_SIZE) {
                noChildTuples = ((noChildTuples - 1) / Boost.MAX_TUPLE_SIZE) + 1;
                childTupleSize *= Boost.MAX_TUPLE_SIZE;
            }

            final List<TypeUsage> splitTypes = new ArrayList<>(noChildTuples);
            childTuples = new ArrayList<>(noChildTuples);
            for (int i = 0; i < types.size(); i += childTupleSize) {
                final BigTuple childTuple = new BigTuple(types.subList(i, Math.min(i + childTupleSize, types.size())));
                childTuples.add(childTuple);
                splitTypes.add(childTuple.getTupleType());
            }
            tupleType = new TypeUsage(Boost.getTupleType(splitTypes));
        } else {
            childTupleSize = 1;
            tupleType = new TypeUsage(Boost.getTupleType(types));
        }

    }

    /**
     * Returns an expression which referencing a single element of a tuple. Boost
     * tuple elements are referenced by calling a templated function {@code
     * get<X>} on the tuple, where X is the index of the required element. Because
     * BigTuple nests boost tuples, we need to nest calls to {@code get<X>} until we
     * reach the desired element. For example to reference element 12 of a 15
     * element tuple would need {@code tuple.get<1>().get<2>()}.
     * <p>
     * <p>
     * the tuple from which an element is required
     * <p>
     * the index of the required element
     *
     * @return an expression representing the required element
     */
    public Expression getTupleGetter(final Expression lhs, final int index) {
        final Function getter = new Function("get");

        if (childTupleSize == 1) {
            getter.addTemplateSpecialisation(new Literal(index));
            return getter.asFunctionCall(lhs, false);
        } else {
            getter.addTemplateSpecialisation(new Literal(index / childTupleSize));
            final Expression newLhs = getter.asFunctionCall(lhs, false);
            return childTuples.get((index / childTupleSize) % childTuples.size()).getTupleGetter(newLhs,
                                                                                                 index %
                                                                                                 childTupleSize);
        }
    }

    public TypeUsage getTupleType() {
        return tupleType;
    }

    private ArrayList<BigTuple> childTuples;

    private final TypeUsage tupleType;

    private int childTupleSize;

}
