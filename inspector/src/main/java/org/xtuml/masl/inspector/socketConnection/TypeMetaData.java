// 
// Filename : TypeMetaData.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;
import java.util.EnumMap;

import org.xtuml.masl.inspector.processInterface.DataValue;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;

public class TypeMetaData extends org.xtuml.masl.inspector.processInterface.TypeMetaData implements ReadableObject {

    // Types must be in same order as the type enumerate in the
    // metadata header
    private static final BasicType typeDecoder[] = new BasicType[] { BasicType.AnyInstance, BasicType.Boolean,
            BasicType.Byte, BasicType.Character, BasicType.Device, BasicType.Duration, BasicType.Enumeration,
            BasicType.Event, BasicType.Instance, BasicType.Integer, BasicType.LongInteger, BasicType.LongNatural,
            BasicType.Natural, BasicType.Real, BasicType.State, BasicType.String, BasicType.Structure,
            BasicType.Timestamp, BasicType.WCharacter, BasicType.WString, BasicType.Timer, BasicType.Dictionary };

    private BasicType basicType;
    private int domainId;
    private int typeId;
    private int collectionDepth;
    private TypeMetaData dictionaryKey;
    private TypeMetaData dictionaryValue;
    private DomainMetaData domain;

    @Override
    public BasicType getBasicType() {
        return basicType;
    }

    @Override
    public int getCollectionDepth() {
        return collectionDepth;
    }

    @Override
    public DataValue<?> getDataObject() {
        if (collectionDepth > 0) {
            return new CollectionData(this.getContainedType());
        } else {
            switch (basicType) {
            case AnyInstance:
                return new AnyInstanceData();
            case Boolean:
                return new BooleanData();
            case Byte:
                return new ByteData();
            case Character:
                return new CharacterData();
            case Device:
                return new DeviceData();
            case Duration:
                return new DurationData();
            case Enumeration:
                return new EnumerateData(this.getEnumerate());
            case Event:
                return new AnyEventData();
            case Instance:
                return new InstanceIdData(this.getObject());
            case Integer:
                return new IntegerData();
            case LongInteger:
                return new LongData();
            case LongNatural:
                return new LongNaturalData();
            case Natural:
                return new NaturalData();
            case Real:
                return new RealData();
            case State:
                return new StateData(this.getObject());
            case String:
                return new StringData();
            case Structure:
                return new StructureData(this.getStructure());
            case Timestamp:
                return new TimestampData();
            case WCharacter:
                return new CharacterData();
            case WString:
                return new StringData();
            case Timer:
                return new TimerData(domain);
            case Dictionary:
                return new DictionaryData(this.getDictionaryKey(), this.getDictionaryValue());
            default:
                throw new IllegalStateException("Unrecognised type " + basicType);
            }
        }
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {

        basicType = typeDecoder[channel.readInt()];
        collectionDepth = channel.readInt();

        switch (basicType) {
        case State:
        case Structure:
        case Enumeration:
        case Instance:
            domainId = channel.readInt();
            typeId = channel.readInt();
            break;
        case Dictionary:
            dictionaryKey = channel.readData(TypeMetaData.class);
            dictionaryValue = channel.readData(TypeMetaData.class);
            break;
        default:
        }
    }

    @Override
    public DomainMetaData getDomain() {
        return domain;
    }

    public void setDomain(final DomainMetaData domain) {
        this.domain = domain;
    }

    @Override
    public StructureMetaData getStructure() {
        return ProcessConnection.getConnection().getMetaData().getDomain(domainId).getStructure(typeId);
    }

    @Override
    public EnumerateMetaData getEnumerate() {
        return ProcessConnection.getConnection().getMetaData().getDomain(domainId).getEnumerate(typeId);
    }

    @Override
    public ObjectMetaData getObject() {
        return ProcessConnection.getConnection().getMetaData().getDomain(domainId).getObject(typeId);
    }

    @Override
    public TypeMetaData getContainedType() {
        if (collectionDepth == 0) {
            return null;
        } else {
            return new TypeMetaData(basicType, domainId, typeId, collectionDepth - 1);
        }
    }

    @Override
    public TypeMetaData getDictionaryKey() {
        return dictionaryKey;
    }

    @Override
    public TypeMetaData getDictionaryValue() {
        return dictionaryValue;
    }

    static TypeMetaData createInstanceType(final int domainId, final int objectId) {
        return new TypeMetaData(BasicType.Instance, domainId, objectId, 0);
    }

    private TypeMetaData(final BasicType storageType, final int domainId, final int typeId, final int collectionDepth) {
        this.basicType = storageType;
        this.domainId = domainId;
        this.typeId = typeId;
        this.collectionDepth = collectionDepth;
    }

    public TypeMetaData() {
    }

    @Override
    public Class<?> getStorageClass() {
        if (getCollectionDepth() > 0) {
            return CollectionData.class;
        } else {
            return storageClasses.get(getBasicType());
        }
    }

    static private EnumMap<BasicType, Class<?>> storageClasses = new EnumMap<BasicType, Class<?>>(BasicType.class);

    static {
        storageClasses.put(BasicType.Boolean, BooleanData.class);
        storageClasses.put(BasicType.Byte, ByteData.class);
        storageClasses.put(BasicType.Character, CharacterData.class);
        storageClasses.put(BasicType.Device, DeviceData.class);
        storageClasses.put(BasicType.Duration, DurationData.class);
        storageClasses.put(BasicType.Enumeration, EnumerateData.class);
        storageClasses.put(BasicType.Instance, InstanceIdData.class);
        storageClasses.put(BasicType.Integer, IntegerData.class);
        storageClasses.put(BasicType.LongInteger, LongData.class);
        storageClasses.put(BasicType.LongNatural, LongNaturalData.class);
        storageClasses.put(BasicType.Natural, NaturalData.class);
        storageClasses.put(BasicType.Real, RealData.class);
        storageClasses.put(BasicType.State, StateData.class);
        storageClasses.put(BasicType.String, StringData.class);
        storageClasses.put(BasicType.Structure, StructureData.class);
        storageClasses.put(BasicType.Timestamp, TimestampData.class);
        storageClasses.put(BasicType.WCharacter, CharacterData.class);
        storageClasses.put(BasicType.WString, StringData.class);
        storageClasses.put(BasicType.Timer, TimerData.class);
        storageClasses.put(BasicType.Dictionary, DictionaryData.class);
    }

}
