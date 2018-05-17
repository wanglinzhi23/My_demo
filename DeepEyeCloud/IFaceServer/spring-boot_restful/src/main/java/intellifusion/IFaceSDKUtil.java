package intellifusion;

public class IFaceSDKUtil {

    static SWIGTYPE_p_void v_instance = null;
    static SWIGTYPE_p_void d_instance = null;

    public static void getInstance() {
        v_instance = IFaceSDK.face_verify_create_instance();
        System.out.println("v_instance : " + v_instance);
        d_instance = IFaceSDK.face_detect_create_instance();
        System.out.println("d_instance : " + v_instance);
    }

    public static double verify(float[] face1_feature, float[] face2_feature) {
//		Date t1 = new Date();
        IF_FACEFEAT face1 = new IF_FACEFEAT();
        IF_FACEFEAT face2 = new IF_FACEFEAT();

        face1.setFeature(face1_feature);
        face2.setFeature(face2_feature);

        float[] b = new float[1];
//		Date t2 = new Date();
//		System.out.println("instance 耗时:"+(t2.getTime()-t1.getTime()) +" ms");

//		 t1 = new Date();
        IFaceSDK.face_verify_pca_feature(v_instance, face1, face2, b);

        face1.delete();
        face2.delete();
//		IFaceSDK.face_verify_release_instance(instance);
//		 t2 = new Date();
//		System.out.println("face_feature_verify 耗时:"+(t2.getTime()-t1.getTime()) +" ms");
//		return b[0]+20;
        return b[0];
//		double temp = Math.exp((b[0]+56)/12);
//		return 100*temp/(temp+1);
    }
}
