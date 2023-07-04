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

import java.util.Arrays;
import java.util.List;

/**
 * Allows creation of various classes from the C++ standard library.
 */
public class Std {

    private final static Library system = new Library(null);

    private final static CodeFile iostreamInc = system.createSystemHeader("iostream");
    private final static CodeFile stringInc = system.createSystemHeader("string");
    private final static CodeFile exceptionInc = system.createSystemHeader("exception");
    private final static CodeFile stdExceptInc = system.createSystemHeader("stdexcept");
    private final static CodeFile cmath = system.createSystemHeader("cmath");
    private final static CodeFile cstdlib = system.createSystemHeader("cstdlib");
    private final static CodeFile setInc = system.createSystemHeader("set");
    private final static CodeFile mapInc = system.createSystemHeader("map");
    private final static CodeFile vectorInc = system.createSystemHeader("vector");
    private final static CodeFile dequeInc = system.createSystemHeader("deque");
    private final static CodeFile limitsInc = system.createSystemHeader("limits");
    private final static CodeFile utilityInc = system.createSystemHeader("utility");
    private final static CodeFile cstddef = system.createSystemHeader("cstddef");
    private final static CodeFile sstreamInc = system.createSystemHeader("sstream");
    private final static CodeFile iteratorInc = system.createSystemHeader("iterator");
    private final static CodeFile algorithmInc = system.createSystemHeader("algorithm");

    /**
     * The <code>::std</code> namespace
     */
    public final static Namespace std = new Namespace("std");
    public final static Namespace gnu_cxx = new Namespace("__gnu_cxx");

    /**
     * The <code>::std::ostream</code> class
     */
    public final static Class ostream = new Class("ostream", std, iostreamInc);
    public final static Class istream = new Class("istream", std, iostreamInc);

    public final static Class ostringstream = new Class("ostringstream", std, sstreamInc);
    public final static Class istringstream = new Class("istringstream", std, sstreamInc);

    /**
     * The <code>::std::string</code> class
     */
    public final static Class string = new Class("string", std, stringInc);

    public final static Class basicString(final TypeUsage of) {
        final Class ret = new Class("basic_string", std, stringInc);
        ret.addTemplateSpecialisation(of);
        return ret;
    }

    /**
     * The <code>::std::wstring</code> class
     */
    public final static Class wstring = new Class("wstring", std, stringInc);

    public final static Class size_t = new Class("size_t", std, cstddef);

    /**
     * Creates a <code>{@literal ::std::pair<first,second>}</code> class
     * <p>
     * <p>
     * the first template parameter
     * <p>
     * the second template parameter
     *
     * @return the pair class
     */
    public final static Class pair(final TypeUsage first, final TypeUsage second) {
        final Class ret = new Class("pair", std, utilityInc);
        ret.addTemplateSpecialisation(first);
        ret.addTemplateSpecialisation(second);
        return ret;
    }

    /**
     * Creates a <code>{@literal ::std::make_pair(first,second)}</code> method
     * call
     * <p>
     * <p>
     * the first argument
     * <p>
     * the second argument
     *
     * @return a function call representing a call to the std::make_pair utility
     * function.
     */
    public final static FunctionCall make_pair(final Variable first, final Variable second) {
        final Function makePair = new Function("make_pair", std, utilityInc);
        final FunctionCall makePairCall = makePair.asFunctionCall(first.asExpression(), second.asExpression());
        return makePairCall;
    }

    /**
     * Creates <code>{@literal ::std::for_each(coll.begin(),coll.end(),fn)}</code>
     * code
     * <p>
     * <p>
     * the collection to iterate over
     * <p>
     * the function object to call
     *
     * @return the code
     */
    public final static Statement for_each(final Expression coll, final Expression fn) {
        return new Function("for_each", std, algorithmInc).asFunctionCall(new Function("begin").asFunctionCall(coll,
                                                                                                               false),
                                                                          new Function("end").asFunctionCall(coll,
                                                                                                             false),
                                                                          fn).asStatement();
    }

    /**
     * Creates a <code>{@literal ::std::map<key,value>}</code> class
     * <p>
     * <p>
     * the index template parameter
     * <p>
     * the value template parameter
     *
     * @return the map class
     */
    public final static Class map(final TypeUsage key, final TypeUsage value) {
        final Class ret = new Class("map", std, mapInc);
        ret.addTemplateSpecialisation(key);
        ret.addTemplateSpecialisation(value);
        return ret;
    }

    /**
     * Creates a <code>{@literal ::std::multimap<key,value>}</code> class
     * <p>
     * <p>
     * the index template parameter
     * <p>
     * the value template parameter
     *
     * @return the multimap class
     */
    public final static Class multimap(final TypeUsage key, final TypeUsage value) {
        final Class ret = new Class("multimap", std, mapInc);
        ret.addTemplateSpecialisation(key);
        ret.addTemplateSpecialisation(value);
        return ret;
    }

    /**
     * Creates a <code>{@literal ::std::set<value>}</code> class
     * <p>
     * <p>
     * the value template parameter
     *
     * @return the set class
     */
    public final static Class set(final TypeUsage value) {
        final Class ret = new Class("set", std, setInc);
        ret.addTemplateSpecialisation(value);
        return ret;
    }

    /**
     * Creates a <code>{@literal ::std::multiset<value>}</code> class
     * <p>
     * <p>
     * the value template parameter
     *
     * @return the multiset class
     */
    public final static Class multiset(final TypeUsage value) {
        final Class ret = new Class("multiset", std, setInc);
        ret.addTemplateSpecialisation(value);
        return ret;
    }

    /**
     * Creates a <code>{@literal ::std::vector<value>}</code> class
     * <p>
     * <p>
     * the value template parameter
     *
     * @return the vector class
     */
    public final static Class vector(final TypeUsage value) {
        final Class ret = new Class("vector", std, vectorInc);
        ret.addTemplateSpecialisation(value);
        return ret;
    }

    /**
     * Creates a <code>{@literal ::std::deque<value>}</code> class
     * <p>
     * <p>
     * the value template parameter
     *
     * @return the deque class
     */
    public final static Class deque(final TypeUsage value) {
        final Class ret = new Class("deque", std, dequeInc);
        ret.addTemplateSpecialisation(value);
        return ret;
    }

    public static Class reverseIterator(final Class iterator) {
        final Class ret = new Class("reverse_iterator", std, iteratorInc);
        ret.addTemplateSpecialisation(new TypeUsage(iterator));
        return ret;
    }

    /**
     * The <code>::std::flush</code> expression
     */
    public final static Expression cout = new Variable("cout", std, iostreamInc).asExpression();

    public final static Expression cin = new Variable("cin", std, iostreamInc).asExpression();

    /**
     * The <code>::std::flush</code> expression
     */
    public final static Expression flush = new Variable("flush", std, iostreamInc).asExpression();

    /**
     * The <code>::std::endl</code> expression
     */
    public final static Expression endl = new Variable("endl", std, iostreamInc).asExpression();

    /**
     * The <code>::std::ws</code> expression
     */
    public final static Expression ws = new Variable("ws", std, iostreamInc).asExpression();

    public static final Expression getline(final Expression stream, final Expression dest) {
        return new Function("getline", std, iostreamInc).asFunctionCall(stream, dest);
    }

    /**
     * The <code>::std::out_of_range</code> exception class
     */
    public final static Class exception = new Class("exception", std, exceptionInc);

    public final static Class OutOfRangeError = new Class("out_of_range", std, stdExceptInc);
    public final static Class RuntimeError = new Class("runtime_error", std, stdExceptInc);

    /**
     * The <code>::std::pow</code> function
     */
    public final static Function pow = new Function("pow", std, cmath);

    /**
     * The <code>::std::abs</code> function
     */
    public final static Function abs = new Function("abs", std, cmath);

    // Two declarations of abs, one in cmath (for floating point types) and one in
    // cstdlib (for integer types), so include both to make sure we get the
    // correct one. In theory we could work out which one was required if we could
    // evaluate the type of any expression, but that is too much like hard work -
    // we're writing a code generator, not a compiler.
    static {
        abs.getDeclaration().addDeclaredIn(cstdlib);
    }

    public static Expression max(final Expression a, final Expression b) {
        return new Function("max", std, algorithmInc).asFunctionCall(a, b);
    }

    public static Expression min(final Expression a, final Expression b) {
        return new Function("max", std, algorithmInc).asFunctionCall(a, b);
    }

    /**
     * Creates a <code>{@literal ::std::max<type>}</code> class
     * <p>
     * <p>
     * the type template parameter
     *
     * @return the max function
     */
    public static Expression numericLimitMax(final TypeUsage type) {
        final Class numericLimits = new Class("numeric_limits", std, limitsInc);
        numericLimits.addTemplateSpecialisation(type);
        return numericLimits.callStaticFunction("max");
    }

    /**
     * Creates a <code>{@literal ::std::min<type>}</code> class
     * <p>
     * <p>
     * the type template parameter
     *
     * @return the min function
     */
    public static Expression numericLimitMin(final TypeUsage type) {
        final Class numericLimits = new Class("numeric_limits", std, limitsInc);
        numericLimits.addTemplateSpecialisation(type);
        return numericLimits.callStaticFunction("min");
    }

    /**
     * Creates a <code>{@literal const_cast<type>}</code> function
     * <p>
     * <p>
     * the type template parameter
     *
     * @return the const_cast function
     */
    public static Function const_cast(final TypeUsage type) {
        final Function const_cast = new Function("const_cast");
        const_cast.addTemplateSpecialisation(type);
        return const_cast;
    }

    /**
     * Creates a <code>{@literal const_cast<type>}</code> function
     * <p>
     * <p>
     * the type template parameter
     *
     * @return the const_cast function
     */
    public static Function static_cast(final TypeUsage type) {
        final Function const_cast = new Function("static_cast");
        const_cast.addTemplateSpecialisation(type);
        return const_cast;
    }

    /**
     * Creates a <code>{@literal const_cast<type>}</code> function
     * <p>
     * <p>
     * the type template parameter
     *
     * @return the const_cast function
     */
    public static Function dynamic_cast(final TypeUsage type) {
        final Function dynamic_cast = new Function("dynamic_cast");
        dynamic_cast.addTemplateSpecialisation(type);
        return dynamic_cast;
    }

    /**
     * Define a recursive method that can be used to form a series of suitable c++
     * ostream operater calls using the supplied list of expressions.
     * <p>
     * <p>
     * A list of expressions to use with the ostream (<<) operator
     *
     * @return A complex expression that represents the required line of C++ to
     * generate.
     */
    public static Expression ostreamExpression(final List<Expression> args) {
        if (args.size() == 1) {
            return args.get(0);
        } else {
            final List<Expression> subExpressionList = args.subList(0, args.size() - 1);
            final Expression tailExpr = args.get(args.size() - 1);
            return new BinaryExpression(ostreamExpression(subExpressionList), BinaryOperator.LEFT_SHIFT, tailExpr);
        }
    }

    public static Expression ostreamExpression(final Expression... args) {
        return ostreamExpression(Arrays.asList(args));
    }

    // Define types for specified size ints - note that the Fundamental type here
    // doesn't matter - it is only used to determine pass by reference
    private static final CodeFile stdintInc = system.createSystemHeader("stdint.h");

    public static final TypedefType
            int8 =
            new TypedefType("int8_t", null, new TypeUsage(FundamentalType.SCHAR), stdintInc);
    public static final TypedefType
            uint8 =
            new TypedefType("uint8_t", null, new TypeUsage(FundamentalType.UCHAR), stdintInc);

    public static final TypedefType
            int32 =
            new TypedefType("int32_t", null, new TypeUsage(FundamentalType.INT), stdintInc);
    public static final TypedefType
            uint32 =
            new TypedefType("uint32_t", null, new TypeUsage(FundamentalType.UINT), stdintInc);

    public static final TypedefType
            int64 =
            new TypedefType("int64_t", null, new TypeUsage(FundamentalType.LONG), stdintInc);
    public static final TypedefType
            uint64 =
            new TypedefType("uint64_t", null, new TypeUsage(FundamentalType.ULONG), stdintInc);

}
