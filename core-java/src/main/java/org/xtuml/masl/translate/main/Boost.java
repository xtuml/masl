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

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.translate.build.BuildSet;

import java.util.List;

public final class Boost {
    private static final BuildSet buildSet = new BuildSet("Boost");

    public final static Library core = new Library("boost").inBuildSet(buildSet);

    public final static Namespace NAMESPACE = new Namespace("boost");
    public final static Namespace LAMBDA = new Namespace("lambda", NAMESPACE);
    public final static Namespace SIGNAL = new Namespace("signals2", NAMESPACE);
    public final static Namespace FORMAT = new Namespace("format", NAMESPACE);

    public final static CodeFile formatInc = core.createInterfaceHeader("boost/format.hpp");
    public final static Function format = new Function("format", NAMESPACE, formatInc);

    public final static CodeFile functionInc = core.createInterfaceHeader("boost/function.hpp");

    public final static CodeFile lambdaInc = core.createInterfaceHeader("boost/lambda/lambda.hpp");

    public final static CodeFile lambdaBindInc = core.createInterfaceHeader("boost/lambda/bind.hpp");

    public final static Expression lambda_1 = new Variable("_1", LAMBDA, lambdaInc).asExpression();
    public final static Expression lambda_2 = new Variable("_2", LAMBDA, lambdaInc).asExpression();
    public final static Expression lambda_3 = new Variable("_3", LAMBDA, lambdaInc).asExpression();
    public final static Expression lambda_4 = new Variable("_4", LAMBDA, lambdaInc).asExpression();

    public final static Function lambda_bind = new Function("bind", LAMBDA, lambdaBindInc);

    public final static CodeFile bindInc = core.createInterfaceHeader("boost/bind.hpp");

    public final static Expression bind_1 = new Variable("_1", null, bindInc).asExpression();
    public final static Expression bind_2 = new Variable("_2", null, bindInc).asExpression();
    public final static Expression bind_3 = new Variable("_3", null, bindInc).asExpression();
    public final static Expression bind_4 = new Variable("_4", null, bindInc).asExpression();
    public final static Expression bind_5 = new Variable("_5", null, bindInc).asExpression();

    public static final int MAX_BIND_PARAMS = 9;

    public static final CodeFile boostSignalInc = core.createInterfaceHeader("boost/signal.hpp");
    public static final Class boostConnection = new Class("signals::connection", NAMESPACE, boostSignalInc);

    public final static Function bind = new Function("bind", NAMESPACE, bindInc);

    public final static CodeFile refInc = core.createInterfaceHeader("boost/ref.hpp");
    public final static Function ref = new Function("ref", NAMESPACE, refInc);

    public final static CodeFile lexicalCastInc = core.createInterfaceHeader("boost/lexical_cast.hpp");

    public static Expression lexicalCast(final TypeUsage to, final Expression from) {
        final Function fn = new Function("lexical_cast", NAMESPACE, lexicalCastInc);
        fn.addTemplateSpecialisation(to);
        return fn.asFunctionCall(from);
    }

    private final static CodeFile hashInc = core.createInterfaceHeader("boost/functional/hash.hpp");

    public static Class getHashType(final TypeUsage type) {
        final Class hash = new Class("hash", NAMESPACE, hashInc);
        hash.addTemplateSpecialisation(type);
        return hash;
    }

    public static Function hash_combine = new Function("hash_combine", NAMESPACE, hashInc);

    private final static CodeFile unorderedMapInc = core.createInterfaceHeader("boost/unordered_map.hpp");

    public static Class unordered_map(final TypeUsage key, final TypeUsage value) {
        final Class ret = new Class("unordered_map", NAMESPACE, unorderedMapInc);
        ret.addTemplateSpecialisation(key);
        ret.addTemplateSpecialisation(value);
        return ret;
    }

    private final static CodeFile unorderedSetInc = core.createInterfaceHeader("boost/unordered_set.hpp");

    public static Class unordered_set(final TypeUsage key) {
        final Class ret = new Class("unordered_set", NAMESPACE, unorderedSetInc);
        ret.addTemplateSpecialisation(key);
        return ret;
    }

    private final static CodeFile boostArrayInc = core.createInterfaceHeader("boost/array.hpp");

    public static Class array(final TypeUsage of, final Expression size) {
        final Class ret = new Class("array", NAMESPACE, boostArrayInc);
        ret.addTemplateSpecialisation(of);
        ret.addTemplateSpecialisation(size);
        return ret;
    }

    private final static CodeFile sharedPtrInc = core.createInterfaceHeader("boost/shared_ptr.hpp");

    public static Class getSharedPtrType(final TypeUsage type) {
        final Class ptr = new Class("shared_ptr", NAMESPACE, sharedPtrInc);
        ptr.addTemplateSpecialisation(type);
        return ptr;
    }

    private final static CodeFile tupleInc = core.createInterfaceHeader("boost/tuple/tuple_comparison.hpp");
    private final static CodeFile tupleHashInc = core.createInterfaceHeader("swa/tuple_hash.hh");
    public final static Function makeTuple = new Function("make_tuple", NAMESPACE, tupleInc);
    public static final int MAX_TUPLE_SIZE = 10;

    public static Class getTupleType(final List<TypeUsage> types) {
        final Class tuple = new Class("tuple", NAMESPACE, tupleInc);
        tuple.addDeclaredIn(tupleHashInc);

        for (final TypeUsage type : types) {
            tuple.addTemplateSpecialisation(type);
        }
        return tuple;
    }

    private final static CodeFile signalInc = core.createInterfaceHeader("boost/signals2.hpp");

    public static final Class signalConnection = new Class("connection", SIGNAL, signalInc);

    public final static CodeFile operatorInc = core.createInterfaceHeader("boost/operators.hpp");

    public static Class lessThanComparable(final TypeUsage type) {
        final Class ret = new Class("less_than_comparable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        return ret;
    }

    public static Class lessThanComparable(final TypeUsage type, final Class chained) {
        final Class ret = new Class("less_than_comparable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        ret.addTemplateSpecialisation(new TypeUsage(chained));
        return ret;
    }

    public static Class equalityComparable(final TypeUsage type) {
        final Class ret = new Class("equality_comparable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        return ret;
    }

    public static Class equalityComparable(final TypeUsage type, final Class chained) {
        final Class ret = new Class("equality_comparable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        ret.addTemplateSpecialisation(new TypeUsage(chained));
        return ret;
    }

    public static Class incrementable(final TypeUsage type) {
        final Class ret = new Class("incrementable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        return ret;
    }

    public static Class incrementable(final TypeUsage type, final Class chained) {
        final Class ret = new Class("incrementable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        ret.addTemplateSpecialisation(new TypeUsage(chained));
        return ret;
    }

    public static Class decrementable(final TypeUsage type) {
        final Class ret = new Class("decrementable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        return ret;
    }

    public static Class decrementable(final TypeUsage type, final Class chained) {
        final Class ret = new Class("decrementable", NAMESPACE, operatorInc);
        ret.addTemplateSpecialisation(type);
        ret.addTemplateSpecialisation(new TypeUsage(chained));
        return ret;
    }

    public static Class functionClass(final TypeUsage returnType, final List<TypeUsage> paramTypeList) {
        final Class ret = new Class("function", NAMESPACE, functionInc);

        final FunctionPtrType funcPtr = new FunctionPtrType();
        funcPtr.addReturnType(returnType);
        funcPtr.addParameterType(paramTypeList);

        ret.addTemplateSpecialisation(new TypeUsage(funcPtr));
        return ret;
    }

    public static Expression dynamic_pointer_cast(final TypeUsage type, final Expression expression) {
        final Function cast = new Function("dynamic_pointer_cast", NAMESPACE, sharedPtrInc);
        cast.addTemplateSpecialisation(type);
        return cast.asFunctionCall(expression);
    }

}
