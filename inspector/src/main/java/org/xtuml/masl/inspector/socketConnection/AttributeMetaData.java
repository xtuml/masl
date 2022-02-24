//
// Filename : AttributeMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class AttributeMetaData extends org.xtuml.masl.inspector.processInterface.AttributeMetaData
        implements ReadableObject {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObjectMetaData getObject() {
        return object;
    }

    @Override
    public String getRelationshipText() {
        final StringBuilder text = new StringBuilder();
        String separator = "";
        for (final int referential : referentials) {
            String number;
            final RelationshipMetaData relationship = getObject().getDomain().getRelationship(referential);

            number = relationship == null ? getObject().getDomain().getSuperSubtype(referential).getNumber()
                    : relationship.getNumber();

            text.append(separator + number);
            separator = ",";
        }
        return text.toString();
    }

    @Override
    public StructureMetaData getStructure() {
        return structure;
    }

    @Override
    public TypeMetaData getType() {
        return type;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public DataValue<?> getDefaultValue() {
        final DataValue<?> defaultValue = type.getDataObject();
        try {
            defaultValue.fromString(defaultValStr);
        } catch (final Exception e) {
        }
        return defaultValue;
    }

    @Override
    public boolean isIdentifier() {
        return identifier;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public boolean isReferential() {
        return referentials.length > 0;
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        name = channel.readString();
        identifier = channel.readBoolean();
        typeName = channel.readString();
        type = channel.readData(TypeMetaData.class);

        defaultValStr = channel.readString();

        referentials = channel.readIntArray();

        // Can be edited if it is not a referential attribute, or it is an
        // identifier
        readOnly = !identifier && isReferential();
    }

    public void setObject(final ObjectMetaData object) {
        this.object = object;
        this.type.setDomain(object.getDomain());
    }

    public void setStructure(final StructureMetaData structure) {
        this.structure = structure;
        this.type.setDomain(structure.getDomain());
    }

    private TypeMetaData type;

    private String typeName;

    private String name;

    private int[] referentials;

    private boolean readOnly;

    private boolean identifier;

    private ObjectMetaData object = null;

    private StructureMetaData structure = null;

    private String defaultValStr;

    public void setDomain(final DomainMetaData domain) {
        type.setDomain(domain);
    }

}
