package intellif.utils;

import intellif.ifaas.t_if_rect_t;
import intellif.settings.ImageSettings;
import intellifusion.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.opencv_core.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;

public class IFaceSDKUtil {

    static SWIGTYPE_p_void v_instance = null;
    static SWIGTYPE_p_void d_instance = null;
    private static Logger LOG = LogManager.getLogger(IFaceSDKUtil.class);

    public static void getInstance() {
        v_instance = IFaceSDK.face_verify_create_instance();
        System.out.println("v_instance : " + v_instance);
        d_instance = IFaceSDK.face_detect_create_instance();
        System.out.println("d_instance : " + v_instance);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List detect(String image_path) {
        List faceList = new ArrayList();
        List face;

        IfRectArray f1 = new IfRectArray(10);
        IfFeatArray f2 = new IfFeatArray(10);

        int face_num = IFaceSDK.face_detect_and_feature_extract(d_instance, v_instance, image_path, f1.cast(), f2.cast());

        for (int i = 0; i < face_num; i++) {
            face = new ArrayList();
            face.add(f2.getitem(i).getFeature());
            face.add(f1.getitem(i).getRect().getLeft() + "," + f1.getitem(i).getRect().getRight() + "," + f1.getitem(i).getRect().getTop() + "," + f1.getitem(i).getRect().getBottom());
            faceList.add(face);
            System.out.println("confidence:" + f1.getitem(i).getConfidence());

            StringBuffer sb = new StringBuffer();
            for (int k = 0; k < f2.getitem(i).getFeature().length; k++) {
                sb.append(f2.getitem(i).getFeature()[k]).append("-");
            }
            String s = sb.toString();
            System.out.println("face1_feature ---> " + s);
        }
        return faceList;
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

    /**
     * blob转byte
     *
     * @param blob
     * @return
     */
    public static byte[] blob2ByteArr(Blob blob) {

        byte[] b = null;
        try {
            if (blob != null) {
                long in = 1;
                b = blob.getBytes(in, (int) (blob.length()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    /**
     * 文件复制
     *
     * @param fileFrom
     * @param fileTo
     * @return
     */
    public static boolean copy(String fileFrom, String fileTo) {
        try {
            if (new File(fileTo).getParent() != null) {
                File f = new File(new File(fileTo).getParent());
                if (!f.exists()) f.mkdirs();
            }
            FileInputStream in = new java.io.FileInputStream(fileFrom);
            FileOutputStream out = new FileOutputStream(fileTo);
            byte[] bt = new byte[1024];
            int count;
            while ((count = in.read(bt)) > 0) {
                out.write(bt, 0, count);
            }
            in.close();
            out.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * 截取图像人脸部分
     *
     * @param fileFrom
     * @param fileTo
     * @param faceInfo
     */
    public static String cutImage(String fileFrom, String fileTo, t_if_rect_t faceInfo, Boolean isJar) throws Exception {
//
    	String subRect = "{ 'SubRect':{ 'left':@left,'top':@top,'right':@right,'bottom':@bottom}}";
    	
        int scaleXY = ImageSettings.getFaceScale();
        int offsetX = ImageSettings.getFaceOffsetX();
        int offsetY = ImageSettings.getFaceOffsetY();
        //
//        IplImage srcImg = cvLoadImage(fileFrom);
        BufferedImage srcImg = ImageIO.read(new File(fileFrom));
        LOG.info("faceInfo:" + faceInfo.toString());
//        int addx = (Integer.valueOf(faceInfo.split(",")[1]) - Integer.valueOf(faceInfo.split(",")[0])) / 3;
        int width = (faceInfo.getRight() - faceInfo.getLeft()) * scaleXY;
        int height = (faceInfo.getBottom() - faceInfo.getTop()) * scaleXY;
        int x = faceInfo.left - offsetX, y = faceInfo.top - offsetY, w = width + 2 * offsetX, h = height + 2 * offsetY;
//        int offsetX = (int) (width * (scaleXY - 1) / 2);
//        int offsetY = (int) (height * (scaleXY - 1) / 2);
//        int x = faceInfo.left - offsetX, y = faceInfo.top - offsetY, w = faceInfo.right - faceInfo.left + 2 * offsetX, h = faceInfo.bottom - faceInfo.top + 2 * offsetY;
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        LOG.info("srcImg.width():" + srcImg.getWidth() + ",srcImg.height():" + srcImg.getHeight());
        w = w + x > srcImg.getWidth() ? srcImg.getWidth() - x : w;
        h = h + y > srcImg.getHeight() ? srcImg.getHeight() - y : h;
        LOG.info("cutImage info,x:" + x + ",y:" + y + ",w:" + w + ",h:" + h);
//        IplImage faceImg = cvGetImage(cvGetSubRect(srcImg, cvCreateMatHeader(w, h, CV_8UC1), cvRect(x, y, w, h)), cvCreateImageHeader(cvSize(w, h), 8, 1));
//        int result = cvSaveImage(fileTo, faceImg);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        bi.getGraphics().drawImage(srcImg, 0, 0, w, h, x, y, x + w, y + h, null);
        int result = ImageIO.write(bi, "jpg", new File(fileTo)) ? 1 : 0;
        LOG.info("cutImage fileTo:" + fileTo + ",result:" + result);
        
        subRect = subRect.replace("@left", String.valueOf(faceInfo.left-x)).replace("@top", String.valueOf(faceInfo.top-y)).replace("@right", String.valueOf(faceInfo.right - x)).replace("@bottom", String.valueOf(faceInfo.bottom - y));
        
        return subRect;
    }
    
    /**
     * 截取图像人脸部分
     *
     * @param url,图片来源为URL
     * @param fileTo
     * @param faceInfo
     */
    public static String cutImageFromURL(String url, String fileTo, t_if_rect_t faceInfo, Boolean isJar) throws Exception {
//
        String subRect = "{ 'SubRect':{ 'left':@left,'top':@top,'right':@right,'bottom':@bottom}}";
        
        int scaleXY = ImageSettings.getFaceScale();
        int offsetX = ImageSettings.getFaceOffsetX();
        int offsetY = ImageSettings.getFaceOffsetY();
        //
//        IplImage srcImg = cvLoadImage(fileFrom);
        BufferedImage srcImg = ImageIO.read(new URL(url));
        LOG.info("faceInfo:" + faceInfo.toString());
//        int addx = (Integer.valueOf(faceInfo.split(",")[1]) - Integer.valueOf(faceInfo.split(",")[0])) / 3;
        int width = (faceInfo.getRight() - faceInfo.getLeft()) * scaleXY;
        int height = (faceInfo.getBottom() - faceInfo.getTop()) * scaleXY;
        int x = faceInfo.left - offsetX, y = faceInfo.top - offsetY, w = width + 2 * offsetX, h = height + 2 * offsetY;
//        int offsetX = (int) (width * (scaleXY - 1) / 2);
//        int offsetY = (int) (height * (scaleXY - 1) / 2);
//        int x = faceInfo.left - offsetX, y = faceInfo.top - offsetY, w = faceInfo.right - faceInfo.left + 2 * offsetX, h = faceInfo.bottom - faceInfo.top + 2 * offsetY;
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        LOG.info("srcImg.width():" + srcImg.getWidth() + ",srcImg.height():" + srcImg.getHeight());
        w = w + x > srcImg.getWidth() ? srcImg.getWidth() - x : w;
        h = h + y > srcImg.getHeight() ? srcImg.getHeight() - y : h;
        LOG.info("cutImage info,x:" + x + ",y:" + y + ",w:" + w + ",h:" + h);
//        IplImage faceImg = cvGetImage(cvGetSubRect(srcImg, cvCreateMatHeader(w, h, CV_8UC1), cvRect(x, y, w, h)), cvCreateImageHeader(cvSize(w, h), 8, 1));
//        int result = cvSaveImage(fileTo, faceImg);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        bi.getGraphics().drawImage(srcImg, 0, 0, w, h, x, y, x + w, y + h, null);
        int result = ImageIO.write(bi, "jpg", new File(fileTo)) ? 1 : 0;
        LOG.info("cutImage fileTo:" + fileTo + ",result:" + result);
        
        subRect = subRect.replace("@left", String.valueOf(faceInfo.left-x)).replace("@top", String.valueOf(faceInfo.top-y)).replace("@right", String.valueOf(faceInfo.right - x)).replace("@bottom", String.valueOf(faceInfo.bottom - y));
        
        return subRect;
    }

    /**
     * 绘制框图标注图像人脸部分
     *
     * @param fileFrom
     * @param faceInfo
     */
    public static void drawImage(String fileFrom, String faceInfo) {
        IplImage srcImg = cvLoadImage(fileFrom);
        int x = Integer.valueOf(faceInfo.split(",")[0]) - 10, y = Integer.valueOf(faceInfo.split(",")[2]) - 10, w = Integer.valueOf(faceInfo.split(",")[1]) - Integer.valueOf(faceInfo.split(",")[0]) + 20, h = Integer.valueOf(faceInfo.split(",")[3]) - Integer.valueOf(faceInfo.split(",")[2]) + 20;
        x = x < 0 ? 0 : x;
        y = y < 0 ? 0 : y;
        if (x + w > srcImg.width()) {
            x = srcImg.width() - w;
        }
        if (y + h > srcImg.height()) {
            y = srcImg.height() - h;
        }
        cvRectangle(srcImg, cvPoint(x, y), cvPoint(x + w, y + h), CvScalar.RED, 2, CV_AA, 0);
        cvSaveImage(fileFrom, srcImg);
    }
}
