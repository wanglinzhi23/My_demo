/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package intellifusion;

public class IF_FACERECT {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected IF_FACERECT(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(IF_FACERECT obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        IFaceSDKJNI.delete_IF_FACERECT(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setRect(if_rect_t value) {
    IFaceSDKJNI.IF_FACERECT_Rect_set(swigCPtr, this, if_rect_t.getCPtr(value), value);
  }

  public if_rect_t getRect() {
    long cPtr = IFaceSDKJNI.IF_FACERECT_Rect_get(swigCPtr, this);
    return (cPtr == 0) ? null : new if_rect_t(cPtr, false);
  }

  public void setPose(IF_FacePose value) {
    IFaceSDKJNI.IF_FACERECT_Pose_set(swigCPtr, this, value.swigValue());
  }

  public IF_FacePose getPose() {
    return IF_FacePose.swigToEnum(IFaceSDKJNI.IF_FACERECT_Pose_get(swigCPtr, this));
  }

  public void setConfidence(float value) {
    IFaceSDKJNI.IF_FACERECT_Confidence_set(swigCPtr, this, value);
  }

  public float getConfidence() {
    return IFaceSDKJNI.IF_FACERECT_Confidence_get(swigCPtr, this);
  }

  public IF_FACERECT() {
    this(IFaceSDKJNI.new_IF_FACERECT(), true);
  }

}