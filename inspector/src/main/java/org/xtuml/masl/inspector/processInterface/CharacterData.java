//
// Filename : CharacterData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

public abstract class CharacterData extends SimpleDataValue<Character> {

    private static Character NULL = '\0';

    @Override
    public Class<Character> getValueClass() {
        return Character.class;
    }

    @Override
    public void fromString(final String string) {
        if (string.length() != 1) {
            throw new IllegalArgumentException("Single character string required");
        }
        setValue(string.charAt(0));
    }

    @Override
    protected void setToDefaultValue() {
        setValue(NULL);
    }
}
