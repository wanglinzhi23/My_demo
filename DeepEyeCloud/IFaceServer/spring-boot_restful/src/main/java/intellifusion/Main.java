package intellifusion;

import intellif.utils.IFaceSDKUtil;

public class Main {
	
	static {
		try {
//		System.loadLibrary("intellifusion");
//		System.loadLibrary("facesdk");
//		System.loadLibrary("faceverify");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("初始化... 1");
		System.load("C:\\facesdk.dll");
		System.out.println("初始化... 2");
		System.load("C:\\faceverify.dll");
		System.out.println("初始化... 3");
		System.load("C:\\intellifusion.dll");
		System.out.println("初始化... 4");
		IFaceSDKUtil.getInstance();
		System.out.println("初始化... 5");
		
		IFaceSDKUtil.detect("D:\\MyEclipse\\WorkSpace\\Test\\temp.jpg");
		
//		System.out.println(System.getProperty("java.library.path"));
//		System.out.println("----0.1");
//		System.load("D:\\MyEclipse\\WorkSpace\\Test\\facesdk.dll");
//		System.out.println("----0.3");
//		System.load("D:\\MyEclipse\\WorkSpace\\Test\\faceverify.dll");
//		System.out.println("----0.6");
//		System.load("D:\\MyEclipse\\WorkSpace\\Test\\intellifusion.dll");
//		System.out.println("----1");
//		IfRectArray f1 = new IfRectArray(10);
//		System.out.println(f1.cast());
//
//		IfFeatArray f2 = new IfFeatArray(10);
//		System.out.println(f2.cast());
//		
//		System.out.println("----2");
////		System.out.println(IFaceSDK.face_detect_and_feature_extract("temp.jpg", f1.cast(), f2.cast()));
//		System.out.println("----3");
//		System.out.println("Top ---> "+f1.getitem(0).getRect().getTop());
//		System.out.println("Bottom ---> "+f1.getitem(0).getRect().getBottom());
//		System.out.println("Left ---> "+f1.getitem(0).getRect().getLeft());
//		System.out.println("Right ---> "+f1.getitem(0).getRect().getRight());
//		System.out.println("Confidence ---> "+f1.getitem(0).getConfidence());
//		System.out.println("Feature ---> "+f2.getitem(0).getFeature());
//		
//
//		System.out.println("Top ---> "+f1.getitem(1).getRect().getTop());
//		System.out.println("Bottom ---> "+f1.getitem(1).getRect().getBottom());
//		System.out.println("Left ---> "+f1.getitem(1).getRect().getLeft());
//		System.out.println("Right ---> "+f1.getitem(1).getRect().getRight());
//		System.out.println("Confidence ---> "+f1.getitem(1).getConfidence());
//		System.out.println("Feature ---> "+f2.getitem(1).getFeature());
		
//		float[] a = f2.cast().getFeature();
//		IF_FACEFEAT face1 = new IF_FACEFEAT();
//		IF_FACEFEAT face2 = new IF_FACEFEAT();
////		float temp = 0;
//		for(int i=0;i<f2.cast().getFeature().length;i++) {
//			System.out.println("Feature loop "+i+" ---> "+a[i]);
//		}
//
//		face1.setFeature(a);
//		face2.setFeature(a);
//		
//		
//		float[] b = new float[1];
//		
//		intellifusion.face_feature_verify(face1, face2, b);
//		for(int k = 0; k<b.length;k++) {
//			System.out.println("rs "+k+" :"+b[k]);
//		}
    }
	
	
}
