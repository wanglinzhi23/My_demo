package intellif.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class ImageUtil {
	  private static Logger LOG = LogManager
	            .getLogger(ImageUtil.class);
	
	 /**
		 * 获取图片正确显示需要旋转的角度（顺时针）
		 * 
		 * @return
		 */
		public static int getAngle(InputStream input) {
			int angle = 0;
			Metadata metadata;
			try {
				metadata = JpegMetadataReader.readMetadata(input);
				Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
				if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
					// Exif信息中方向
					int orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
					// 原图片的方向信息
					if (6 == orientation) {
						// 6旋转90
						angle = 90;
					} else if (3 == orientation) {
						// 3旋转180
						angle = 180;

					} else if (8 == orientation) {
						// 8旋转90
						angle = 270;
					}
				}
			} catch (Exception e) {
				LOG.error("getAngle Exception", e);
			}
			return angle;
		}

		
		/**
	     * 旋转图片为指定角度
	     * 
	     * @param bufferedimage
	     *            目标图像
	     * @param degree
	     *            旋转角度
	     * @return
	     */
	    public static BufferedImage rotateImage(byte[] bytes, final int degree,String filename) {
	    	BufferedImage bufferedImage; 
	    	ByteArrayInputStream in = new ByteArrayInputStream(bytes); //将b作为输入流；
	    	BufferedImage img = null;
	    	try {
				bufferedImage = ImageIO.read(in);
				 int w = bufferedImage.getWidth();
			        int h = bufferedImage.getHeight();
			        int type = bufferedImage.getColorModel().getTransparency();
			        Graphics2D graphics2d;
			        (graphics2d = (img = new BufferedImage(w, h, type))
			                .createGraphics()).setRenderingHint(
			                RenderingHints.KEY_INTERPOLATION,
			                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
			        graphics2d.drawImage(bufferedImage, 0, 0, null);
			        graphics2d.dispose();
			        return img;
			} catch (IOException e) {
				LOG.error("{} rotateImage exception", filename, e);
			} 
	        return img;
	    }
	    
	    
	 public static MultipartFile createMultFile(String name,String type, String pathStr){
		 try{
			 Path path =  Paths.get(pathStr);
			 byte[] content = Files.readAllBytes(path);
			 MultipartFile multipartFile = new MockMultipartFile(name, pathStr, type,
					 content);   
			 return multipartFile;
		 }catch(Exception e){
			 LOG.error("createMultFile error,path:"+pathStr,e);
			 return null;
		 }
		 
	 }
}
