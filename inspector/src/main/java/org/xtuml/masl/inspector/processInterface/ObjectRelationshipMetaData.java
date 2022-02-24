//
// Filename : ObjectRelationshipMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class ObjectRelationshipMetaData {

    public abstract String getRolePhrase();

    public abstract ObjectMetaData getDestObject();

    public abstract String getNumber();

    public abstract boolean isMultiple();

    public abstract boolean isConditional();

    public abstract boolean isSuperSubtype();

    public String getCardinalityString() {
        return (isConditional() ? "0" : "1") + (isMultiple() ? "..*" : (isConditional() ? "..1" : ""));
    }

    public String getDescription() {
        return (isSuperSubtype() ? "is a" : getRolePhrase() + " " + getCardinalityString()) + " "
                + getDestObject().getName();
    }

}
