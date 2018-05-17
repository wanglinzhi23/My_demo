/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package intellif.ifaas;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2017-8-31")
public class T_MulAlgFeatureExtReq implements org.apache.thrift.TBase<T_MulAlgFeatureExtReq, T_MulAlgFeatureExtReq._Fields>, java.io.Serializable, Cloneable, Comparable<T_MulAlgFeatureExtReq> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("T_MulAlgFeatureExtReq");

  private static final org.apache.thrift.protocol.TField FACE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("FaceId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField FACE_TAB_FIELD_DESC = new org.apache.thrift.protocol.TField("FaceTab", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField ALG_VERSIONS_FIELD_DESC = new org.apache.thrift.protocol.TField("AlgVersions", org.apache.thrift.protocol.TType.LIST, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new T_MulAlgFeatureExtReqStandardSchemeFactory());
    schemes.put(TupleScheme.class, new T_MulAlgFeatureExtReqTupleSchemeFactory());
  }

  public long FaceId; // required
  public int FaceTab; // required
  public List<Integer> AlgVersions; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    FACE_ID((short)1, "FaceId"),
    FACE_TAB((short)2, "FaceTab"),
    ALG_VERSIONS((short)3, "AlgVersions");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // FACE_ID
          return FACE_ID;
        case 2: // FACE_TAB
          return FACE_TAB;
        case 3: // ALG_VERSIONS
          return ALG_VERSIONS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __FACEID_ISSET_ID = 0;
  private static final int __FACETAB_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.FACE_ID, new org.apache.thrift.meta_data.FieldMetaData("FaceId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.FACE_TAB, new org.apache.thrift.meta_data.FieldMetaData("FaceTab", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.ALG_VERSIONS, new org.apache.thrift.meta_data.FieldMetaData("AlgVersions", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(T_MulAlgFeatureExtReq.class, metaDataMap);
  }

  public T_MulAlgFeatureExtReq() {
  }

  public T_MulAlgFeatureExtReq(
    long FaceId,
    int FaceTab,
    List<Integer> AlgVersions)
  {
    this();
    this.FaceId = FaceId;
    setFaceIdIsSet(true);
    this.FaceTab = FaceTab;
    setFaceTabIsSet(true);
    this.AlgVersions = AlgVersions;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public T_MulAlgFeatureExtReq(T_MulAlgFeatureExtReq other) {
    __isset_bitfield = other.__isset_bitfield;
    this.FaceId = other.FaceId;
    this.FaceTab = other.FaceTab;
    if (other.isSetAlgVersions()) {
      List<Integer> __this__AlgVersions = new ArrayList<Integer>(other.AlgVersions);
      this.AlgVersions = __this__AlgVersions;
    }
  }

  public T_MulAlgFeatureExtReq deepCopy() {
    return new T_MulAlgFeatureExtReq(this);
  }

  @Override
  public void clear() {
    setFaceIdIsSet(false);
    this.FaceId = 0;
    setFaceTabIsSet(false);
    this.FaceTab = 0;
    this.AlgVersions = null;
  }

  public long getFaceId() {
    return this.FaceId;
  }

  public T_MulAlgFeatureExtReq setFaceId(long FaceId) {
    this.FaceId = FaceId;
    setFaceIdIsSet(true);
    return this;
  }

  public void unsetFaceId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __FACEID_ISSET_ID);
  }

  /** Returns true if field FaceId is set (has been assigned a value) and false otherwise */
  public boolean isSetFaceId() {
    return EncodingUtils.testBit(__isset_bitfield, __FACEID_ISSET_ID);
  }

  public void setFaceIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __FACEID_ISSET_ID, value);
  }

  public int getFaceTab() {
    return this.FaceTab;
  }

  public T_MulAlgFeatureExtReq setFaceTab(int FaceTab) {
    this.FaceTab = FaceTab;
    setFaceTabIsSet(true);
    return this;
  }

  public void unsetFaceTab() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __FACETAB_ISSET_ID);
  }

  /** Returns true if field FaceTab is set (has been assigned a value) and false otherwise */
  public boolean isSetFaceTab() {
    return EncodingUtils.testBit(__isset_bitfield, __FACETAB_ISSET_ID);
  }

  public void setFaceTabIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __FACETAB_ISSET_ID, value);
  }

  public int getAlgVersionsSize() {
    return (this.AlgVersions == null) ? 0 : this.AlgVersions.size();
  }

  public java.util.Iterator<Integer> getAlgVersionsIterator() {
    return (this.AlgVersions == null) ? null : this.AlgVersions.iterator();
  }

  public void addToAlgVersions(int elem) {
    if (this.AlgVersions == null) {
      this.AlgVersions = new ArrayList<Integer>();
    }
    this.AlgVersions.add(elem);
  }

  public List<Integer> getAlgVersions() {
    return this.AlgVersions;
  }

  public T_MulAlgFeatureExtReq setAlgVersions(List<Integer> AlgVersions) {
    this.AlgVersions = AlgVersions;
    return this;
  }

  public void unsetAlgVersions() {
    this.AlgVersions = null;
  }

  /** Returns true if field AlgVersions is set (has been assigned a value) and false otherwise */
  public boolean isSetAlgVersions() {
    return this.AlgVersions != null;
  }

  public void setAlgVersionsIsSet(boolean value) {
    if (!value) {
      this.AlgVersions = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case FACE_ID:
      if (value == null) {
        unsetFaceId();
      } else {
        setFaceId((Long)value);
      }
      break;

    case FACE_TAB:
      if (value == null) {
        unsetFaceTab();
      } else {
        setFaceTab((Integer)value);
      }
      break;

    case ALG_VERSIONS:
      if (value == null) {
        unsetAlgVersions();
      } else {
        setAlgVersions((List<Integer>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case FACE_ID:
      return Long.valueOf(getFaceId());

    case FACE_TAB:
      return Integer.valueOf(getFaceTab());

    case ALG_VERSIONS:
      return getAlgVersions();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case FACE_ID:
      return isSetFaceId();
    case FACE_TAB:
      return isSetFaceTab();
    case ALG_VERSIONS:
      return isSetAlgVersions();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof T_MulAlgFeatureExtReq)
      return this.equals((T_MulAlgFeatureExtReq)that);
    return false;
  }

  public boolean equals(T_MulAlgFeatureExtReq that) {
    if (that == null)
      return false;

    boolean this_present_FaceId = true;
    boolean that_present_FaceId = true;
    if (this_present_FaceId || that_present_FaceId) {
      if (!(this_present_FaceId && that_present_FaceId))
        return false;
      if (this.FaceId != that.FaceId)
        return false;
    }

    boolean this_present_FaceTab = true;
    boolean that_present_FaceTab = true;
    if (this_present_FaceTab || that_present_FaceTab) {
      if (!(this_present_FaceTab && that_present_FaceTab))
        return false;
      if (this.FaceTab != that.FaceTab)
        return false;
    }

    boolean this_present_AlgVersions = true && this.isSetAlgVersions();
    boolean that_present_AlgVersions = true && that.isSetAlgVersions();
    if (this_present_AlgVersions || that_present_AlgVersions) {
      if (!(this_present_AlgVersions && that_present_AlgVersions))
        return false;
      if (!this.AlgVersions.equals(that.AlgVersions))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_FaceId = true;
    list.add(present_FaceId);
    if (present_FaceId)
      list.add(FaceId);

    boolean present_FaceTab = true;
    list.add(present_FaceTab);
    if (present_FaceTab)
      list.add(FaceTab);

    boolean present_AlgVersions = true && (isSetAlgVersions());
    list.add(present_AlgVersions);
    if (present_AlgVersions)
      list.add(AlgVersions);

    return list.hashCode();
  }

  @Override
  public int compareTo(T_MulAlgFeatureExtReq other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetFaceId()).compareTo(other.isSetFaceId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFaceId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.FaceId, other.FaceId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFaceTab()).compareTo(other.isSetFaceTab());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFaceTab()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.FaceTab, other.FaceTab);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAlgVersions()).compareTo(other.isSetAlgVersions());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAlgVersions()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.AlgVersions, other.AlgVersions);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("T_MulAlgFeatureExtReq(");
    boolean first = true;

    sb.append("FaceId:");
    sb.append(this.FaceId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("FaceTab:");
    sb.append(this.FaceTab);
    first = false;
    if (!first) sb.append(", ");
    sb.append("AlgVersions:");
    if (this.AlgVersions == null) {
      sb.append("null");
    } else {
      sb.append(this.AlgVersions);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class T_MulAlgFeatureExtReqStandardSchemeFactory implements SchemeFactory {
    public T_MulAlgFeatureExtReqStandardScheme getScheme() {
      return new T_MulAlgFeatureExtReqStandardScheme();
    }
  }

  private static class T_MulAlgFeatureExtReqStandardScheme extends StandardScheme<T_MulAlgFeatureExtReq> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, T_MulAlgFeatureExtReq struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // FACE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.FaceId = iprot.readI64();
              struct.setFaceIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // FACE_TAB
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.FaceTab = iprot.readI32();
              struct.setFaceTabIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // ALG_VERSIONS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.AlgVersions = new ArrayList<Integer>(_list8.size);
                int _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = iprot.readI32();
                  struct.AlgVersions.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setAlgVersionsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, T_MulAlgFeatureExtReq struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(FACE_ID_FIELD_DESC);
      oprot.writeI64(struct.FaceId);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(FACE_TAB_FIELD_DESC);
      oprot.writeI32(struct.FaceTab);
      oprot.writeFieldEnd();
      if (struct.AlgVersions != null) {
        oprot.writeFieldBegin(ALG_VERSIONS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, struct.AlgVersions.size()));
          for (int _iter11 : struct.AlgVersions)
          {
            oprot.writeI32(_iter11);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class T_MulAlgFeatureExtReqTupleSchemeFactory implements SchemeFactory {
    public T_MulAlgFeatureExtReqTupleScheme getScheme() {
      return new T_MulAlgFeatureExtReqTupleScheme();
    }
  }

  private static class T_MulAlgFeatureExtReqTupleScheme extends TupleScheme<T_MulAlgFeatureExtReq> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, T_MulAlgFeatureExtReq struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetFaceId()) {
        optionals.set(0);
      }
      if (struct.isSetFaceTab()) {
        optionals.set(1);
      }
      if (struct.isSetAlgVersions()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetFaceId()) {
        oprot.writeI64(struct.FaceId);
      }
      if (struct.isSetFaceTab()) {
        oprot.writeI32(struct.FaceTab);
      }
      if (struct.isSetAlgVersions()) {
        {
          oprot.writeI32(struct.AlgVersions.size());
          for (int _iter12 : struct.AlgVersions)
          {
            oprot.writeI32(_iter12);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, T_MulAlgFeatureExtReq struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.FaceId = iprot.readI64();
        struct.setFaceIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.FaceTab = iprot.readI32();
        struct.setFaceTabIsSet(true);
      }
      if (incoming.get(2)) {
        {
          org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I32, iprot.readI32());
          struct.AlgVersions = new ArrayList<Integer>(_list13.size);
          int _elem14;
          for (int _i15 = 0; _i15 < _list13.size; ++_i15)
          {
            _elem14 = iprot.readI32();
            struct.AlgVersions.add(_elem14);
          }
        }
        struct.setAlgVersionsIsSet(true);
      }
    }
  }

}
