/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.inmemory;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.translate.building.BuildSet;
import org.xtuml.masl.translate.main.Architecture;

public class Transient {

    public static BuildSet buildSet = new BuildSet("xtuml_transient");
    public static final Library popLibrary = new ImportedLibrary("xtuml_transient").inBuildSet(buildSet);

    public final static Namespace NAMESPACE = new Namespace("transient");

    public final static CodeFile toOneInc = popLibrary.createInterfaceHeader("transient/ToOneRelationship.hh");

    public final static Class toOneRelationship(final TypeUsage related) {
        final Class ret = new Class("ToOneRelationship", NAMESPACE, toOneInc);
        ret.addTemplateSpecialisation(related);
        return ret;
    }

    public final static CodeFile toManyInc = popLibrary.createInterfaceHeader("transient/ToManyRelationship.hh");

    public final static Class toManyRelationship(final TypeUsage related) {
        final Class ret = new Class("ToManyRelationship", NAMESPACE, toManyInc);
        ret.addTemplateSpecialisation(related);
        return ret;
    }

    public final static CodeFile toManyAssocInc = popLibrary.createInterfaceHeader("transient/ToManyAssociative.hh");

    public final static Class toManyAssociative(final TypeUsage related, final TypeUsage assoc) {
        final Class ret = new Class("ToManyAssociative", NAMESPACE, toManyAssocInc);
        ret.addTemplateSpecialisation(related);
        ret.addTemplateSpecialisation(assoc);
        return ret;
    }

    public final static CodeFile toOneAssocInc = popLibrary.createInterfaceHeader("transient/ToOneAssociative.hh");

    public final static Class toOneAssociative(final TypeUsage related, final TypeUsage assoc) {
        final Class ret = new Class("ToOneAssociative", NAMESPACE, toOneAssocInc);
        ret.addTemplateSpecialisation(related);
        ret.addTemplateSpecialisation(assoc);
        return ret;
    }

    public final static CodeFile populationInc = popLibrary.createInterfaceHeader("transient/Population.hh");

    public final static Class population(final TypeUsage object, final TypeUsage factory) {
        final Class ret = new Class("TransientPopulation", NAMESPACE, populationInc);
        ret.addTemplateSpecialisation(object);
        ret.addTemplateSpecialisation(factory);
        return ret;
    }

}
