/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package intellif.ifaas;


import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum E_FACE_FORBIDEN_VALUE implements org.apache.thrift.TEnum {
  FACE_FORBIDEN_VALUE_FALSE(0),
  FACE_FORBIDEN_VALUE_TRUE(1);

  private final int value;

  private E_FACE_FORBIDEN_VALUE(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static E_FACE_FORBIDEN_VALUE findByValue(int value) { 
    switch (value) {
      case 0:
        return FACE_FORBIDEN_VALUE_FALSE;
      case 1:
        return FACE_FORBIDEN_VALUE_TRUE;
      default:
        return null;
    }
  }
}
