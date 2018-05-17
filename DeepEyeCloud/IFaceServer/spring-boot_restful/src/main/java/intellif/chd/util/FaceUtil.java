package intellif.chd.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import intellif.chd.bean.Camera;
import intellif.chd.consts.Constant;
import intellif.chd.vo.Face;
import intellif.dto.CameraDto;
import intellif.dto.FaceResultDto;
import intellif.utils.CommonUtil;
import intellif.database.entity.CameraInfo;
import intellif.database.entity.FaceInfo;

public class FaceUtil {

	private static final Logger LOG = LogManager.getLogger(FaceUtil.class);

	public static final String DEFAULT_CHARSET = "UTF-8";

	public static final String DEFAULT_PATH = new File(FaceUtil.class.getResource("/").getPath()).getParent()
			+ "/output/";

	/**
	 * 浮点数组转字节数组
	 * @param fs
	 * @return
	 */
	public static byte[] float2byte(float... fs) {
		if (null == fs) {
			return null;
		}
		byte[] dest = new byte[4 * fs.length];
		for (int k = 0; k < fs.length; k++) {
			// 把float转换为byte[]
			int fbit = Float.floatToIntBits(fs[k]);

			for (int i = 0; i < 4; i++) {
				dest[4 * k + 3 - i] = (byte) (fbit >> (24 - i * 8));
			}
		}
		return dest;
	}

	/**
	 * 对人脸列表排序并去重
	 * 
	 * @param faceList
	 * @return
	 */
	public static List<Face> sortAndDistinct(List<Face> faceList) {
		if (CollectionUtils.isEmpty(faceList)) {
			return faceList;
		}
//		Collections.sort(faceList, (o1, o2) -> CommonUtil.saftConvert(o1.getTime().compareTo(o2.getTime())));
		faceList.sort((m, n) -> {
			int x = m.getTime().compareTo(n.getTime());
			if (x == 0) {
				int y = CommonUtil.saftConvert(m.getSourceId().compareTo(n.getSourceId()));
				if (y == 0) {
					return CommonUtil.saftConvert(m.getId().compareTo(n.getId()));
				}
				return y;
			} else {
				return x;
			}
		});

		long lastId = -1L;
		Iterator<Face> it = faceList.iterator();
		while (it.hasNext()) {
			Face face = it.next();
			if (face.getId().equals(lastId)) {
				it.remove();
			} else {
				lastId = face.getId();
			}
		}
		return faceList;
	}

	/**
	 * 对人脸列表排序并去重
	 * 
	 * @param faceInfoList
	 * @return
	 */
	public static List<FaceInfo> sortAndDistinctFaceInfoList(List<FaceInfo> faceInfoList) {
		if (CollectionUtils.isEmpty(faceInfoList)) {
			return faceInfoList;
		}
		//Collections.sort(faceInfoList, (o1, o2) -> CommonUtil.saftConvert(o1.getTime().compareTo(o2.getTime())));
		faceInfoList.sort((n, m) -> {
			int x = m.getTime().compareTo(n.getTime());
			if (x == 0) {
				int y = CommonUtil.saftConvert(m.getSourceId() - n.getSourceId());
				if (y == 0) {
					return CommonUtil.saftConvert(m.getId().compareTo(n.getId()));
				}
				return y;
			} else {
				return x;
			}
		});
		long lastId = -1L;
		Iterator<FaceInfo> it = faceInfoList.iterator();
		while (it.hasNext()) {
			FaceInfo face = it.next();
			if (face.getId().equals(lastId)) {
				it.remove();
			} else {
				lastId = face.getId();
			}
		}
		return faceInfoList;
	}

	public static float javaVerify(float[] x, float[] y) {
		if (null == x || null == y) {
			throw new IllegalArgumentException("x is null or y is null");
		}
		if (x.length != y.length) {
			throw new IllegalArgumentException("x.length is " + x.length + ", y.length is " + y.length);
		}

		return javaVerify(x, y, 0, Constant.REAL_LENGTH_181);
	}

	private static boolean isLike(float[] x, float[] y) {
		float ret = 0;
		for (int i = 1; i < 129; i++) {
			ret += x[i] * x[i] + y[i] * y[i] - 4 * x[i] * y[i];
		}
		float retTemp = (float) (1 / (1 + Math.exp(ret)));
		return Float.compare(retTemp, 0.6f) >= 0;
	}

	public static boolean isLike(float[] x, float[] y, float threshold) {
		if (null == x || null == y || x.length < Constant.REAL_LENGTH_181 || y.length < Constant.REAL_LENGTH_181) {
			return false;
		}
		return Float.compare(javaVerify(x, y), threshold) >= 0;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param offset
	 * @param length
	 * @return
	 */
	private static float javaVerify(float[] x, float[] y, int offset, int length) {
		float ret = 0;
		int maxIndex = Math.min(Math.min(x.length, y.length), offset + length);
		for (int i = offset; i < maxIndex; i++) {
			ret += x[i] * x[i] + y[i] * y[i] - 4 * x[i] * y[i];
		}
		float retTemp = (float) (1 / (1 + Math.exp(ret)));
		if (Float.compare(retTemp, 0.5f) <= 0) {
			ret = 3.3342F * retTemp - 0.7671F;
		} else {
			ret = 0.2F * retTemp + 0.8F;
		}
		return ret;
	}

	/**
	 * 字节数组转换为浮点数组
	 * 
	 * @param b
	 *            字节（至少4个字节）
	 * @param offset
	 *            开始位置
	 * @return
	 */
	public static float[] byte2float(byte[] b, int offset, int length) {
		if (null == b || length < 0 || length % 4 != 0) {
			throw new IllegalArgumentException("b is null or length is not valid, length is " + length);

		}
		if (offset + length > b.length || offset < 0 || offset >= b.length) {
			throw new ArrayIndexOutOfBoundsException(
					"b.length is " + b.length + ", offset is " + offset + ", length is " + length);
		}
		float[] x = new float[length / 4];
		for (int i = 0; i < x.length; i++) {
			int l;
			int begin = offset + i * 4;
			l = b[begin];
			l &= 0xff;
			l |= ((long) b[begin + 1] << 8);
			l &= 0xffff;
			l |= ((long) b[begin + 2] << 16);
			l &= 0xffffff;
			l |= ((long) b[begin + 3] << 24);
			x[i] = Float.intBitsToFloat(l);
		}
		return x;
	}


	/**
	 * 将List<FaceResultDto>转成List<FaceInfo>
	 * 
	 * @param faceResultList
	 * @return
	 */
	public static List<FaceInfo> convert(List<FaceResultDto> faceResultList) {
		List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
		if (CollectionUtils.isEmpty(faceResultList)) {
			return faceInfoList;
		}
		faceResultList.forEach((face) -> {
			try {
				faceInfoList.add(convert(face));
			} catch (Throwable e) {
				LOG.warn("Fail to convert FaceResultDto to FaceInfo, FaceResultDto is " + face + ", catch exception: ",
						e);
			}
		});
		return faceInfoList;
	}

	/**
	 * 将FaceResultDto转成FaceInfo
	 * 
	 * @param face
	 * @return
	 */
	public static FaceInfo convert(FaceResultDto face) {
		FaceInfo faceInfo = new FaceInfo();
		faceInfo.setId(Long.valueOf(face.getId()));
		faceInfo.setSourceId(face.getCamera());
		faceInfo.setImageData(face.getFile());
		faceInfo.setTime(face.getTime());
		return faceInfo;
	}

	/**
	 * 将FaceInfo列表转换成Face列表
	 * 
	 * @param faceInfoList
	 *            Face对象
	 * @return FaceInfo对象
	 */
	public static List<Face> convertFaceInfoList(List<FaceInfo> faceInfoList) {
		if (null == faceInfoList) {
			return new ArrayList<>();
		}
		List<Face> faceList = new ArrayList<>();
		for (FaceInfo faceInfo : faceInfoList) {
			faceList.add(convertFaceInfo(faceInfo));
		}
		return faceList;
	}
	
	/**
	 * 将FaceInfo列表转换成Face列表
	 * 
	 * @param FaceResultDtoList
	 *            Face对象
	 * @return FaceInfo对象
	 */
	public static List<Face> convertFaceResultDtoList(List<FaceResultDto> FaceResultDtoList) {
		if (null == FaceResultDtoList) {
			return new ArrayList<>();
		}
		List<Face> faceList = new ArrayList<>();
		for (FaceResultDto faceInfo : FaceResultDtoList) {
			faceList.add(convertFaceResultDto(faceInfo));
		}
		return faceList;
	}

	public static Face convertFaceResultDto(FaceResultDto faceResultDto) {
		if (null == faceResultDto) {
			return null;
		}
		Face face = new Face();
		face.setId(Long.valueOf(faceResultDto.getId()));
		face.setTime(faceResultDto.getTime());
		face.setImageData(faceResultDto.getFile());
		face.setSourceId(faceResultDto.getCamera());
		return face;
	}

	/**
	 * 将Face对象转换成FaceInfo对象
	 * 
	 * @param face
	 *            Face对象
	 * @return FaceInfo对象
	 */
	public static FaceInfo convertFace(Face face) {
		if (null == face) {
			return null;
		}
		FaceInfo faceInfo = new FaceInfo();
		faceInfo.setId(face.getId());
		faceInfo.setImageData(face.getImageData());
		faceInfo.setTime(face.getTime());
		faceInfo.setSourceId(face.getSourceId());
		return faceInfo;
	}

	/**
	 * 将FaceInfo对象转换成Face对象
	 * 
	 * @param faceInfo
	 *            Face对象
	 * @return FaceInfo对象
	 */
	public static Face convertFaceInfo(FaceInfo faceInfo) {
		if (null == faceInfo) {
			return null;
		}
		Face face = new Face();
		face.setId(faceInfo.getId());
		face.setImageData(faceInfo.getImageData());
		face.setTime(faceInfo.getTime());
		face.setSourceId(faceInfo.getSourceId());
		return face;
	}


	/**
	 * 自增
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static void selfPlus(float[] x, float[] y) {
		if (null == x || x.length == 0) {
			return;
		}
		if (null == y || y.length == 0) {
			return;
		}
		for (int i = 0; i < x.length; i++) {
			if (i >= y.length) {
				break;
			}
			x[i] += y[i];
		}
	}

	/**
	 * 自乘
	 * 
	 * @param fb
	 * @param y
	 * @return
	 */
	public static void selfMultiplication(float[] fb, int y) {
		if (null == fb || fb.length == 0 || y == 1) {
			return;
		}
		for (int i = 0; i < fb.length; i++) {
			fb[i] *= y;
		}
	}

	/**
	 * 自除
	 * 
	 * @param fb
	 * @param y
	 * @return
	 */
	public static void selfDivision(float[] fb, int y) {
		if (null == fb || fb.length == 0 || y == 1 || y == 0) {
			return;
		}
		for (int i = 0; i < fb.length; i++) {
			fb[i] /= y;
		}
	}

	/**
	 * 加法
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static float[] plus(float[] x, float[] y) {
		if (null == x || x.length == 0) {
			return y;
		}
		if (null == y || y.length == 0) {
			return x;
		}
		float[] result = new float[Math.max(x.length, y.length)];
		for (int i = 0; i < result.length; i++) {
			if (i >= x.length) {
				result[i] = y[i];
				continue;
			}
			if (i >= y.length) {
				result[i] = x[i];
				continue;
			}
			result[i] = x[i] + y[i];
		}
		return result;
	}

	/**
	 * 乘法
	 * 
	 * @param fb
	 * @param y
	 * @return
	 */
	public static float[] multiplication(float[] fb, int y) {
		if (null == fb || fb.length == 0 || y == 1) {
			return fb;
		}
		float[] result = new float[fb.length];
		for (int i = 0; i < fb.length; i++) {
			result[i] = fb[i] * y;
		}
		return result;
	}

	/**
	 * 除法
	 * 
	 * @param fb
	 * @param y
	 * @return
	 */
	public static float[] division(float[] fb, int y) {
		if (null == fb || fb.length == 0 || y == 1 || y == 0) {
			return fb;
		}
		float[] result = new float[fb.length];
		for (int i = 0; i < fb.length; i++) {
			result[i] = fb[i] / y;
		}
		return result;
	}


	/**
	 * 保存结果到文件
	 * 
	 * @param taskId
	 * @param output
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String saveOutputToFile(long taskId, String output) {
		Validate.notNull(output, "结果为空");
		Validate.isTrue(taskId > 0, "任务ID不合法");
		FileOutputStream fileOutputStream = null;
		GZIPOutputStream gzip = null;
		try {
			// 启用gzip压缩
			String fileName = fileName(taskId);
			File file = new File(fileName);
			file.getParentFile().mkdirs();
			file.createNewFile();
			fileOutputStream = new FileOutputStream(fileName, false);
			gzip = new GZIPOutputStream(fileOutputStream);
			gzip.write(output.getBytes(DEFAULT_CHARSET));
			gzip.finish();
			gzip.flush();
			while (!file.exists() || file.length() == 0) {
				Thread.sleep(1000L);
				LOG.info("xxxxxx wait for file exist");
			}
			return fileName;
		} catch (Exception e) {
			throw new IllegalArgumentException("保存任务结果失败！", e);
		} finally {
			close(gzip);
			close(fileOutputStream);
		}
	}

	/**
	 * 保存结果到文件
	 * 
	 * @param taskId
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void deleteOutputFile(long taskId) {
		try {
			/* 启用gzip压缩 */
			String fileName = fileName(taskId);
			File file = new File(fileName);
			file.delete();
		} catch (Throwable e) {
			LOG.info("Fail to delete " + taskId + " output file, catch exception: ", e);
		}
	}

	/**
	 * 从文件中查询任务结果
	 * 
	 * @param taskId
	 */
	public static String readOutputFromFile(long taskId) {
		Validate.isTrue(taskId > 0, "任务ID不合法");
		FileInputStream in = null;
		GZIPInputStream gzip = null;
		ByteArrayOutputStream bos = null;
		try {
			// 启用gzip压缩
			String fileName = fileName(taskId);
			File file = new File(fileName);
			gzip = new GZIPInputStream(new FileInputStream(file));
			bos = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024 * 16];
			int readLength = -1;
			while ((readLength = gzip.read(buffer)) != -1) {
				bos.write(buffer, 0, readLength);
			}
			return new String(bos.toByteArray(), DEFAULT_CHARSET);
		} catch (Exception e) {
			throw new IllegalArgumentException("获取任务结果失败！", e);
		} finally {
			close(gzip);
			close(bos);
		}
	}

	public static String fileName(long taskId) {
		return DEFAULT_PATH + (taskId / 1000000) + "/" + (taskId / 1000) + "/" + taskId;
	}

	public static void close(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (Throwable e) {
				LOG.error("catch exception: ", e);
			}
		}
	}
	

	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	/**
	 * 解析摄像头列表，将其解析成 摄像头id与摄像头的映射关系
	 * 
	 * @param cameraDtoList
	 * @return
	 */
	public static Map<Long, Camera> parseCameraList(List<CameraInfo> cameraDtoList) {
		Map<Long, Camera> parseResult = new HashMap<>();

		// 校验参数
		if (CollectionUtils.isEmpty(cameraDtoList)) {
			return parseResult;
		}

		// 解析人脸列表
		for (CameraInfo cameraDto : cameraDtoList) {
			if (null != cameraDto) {
				Camera camera = new Camera();
				camera.setId(cameraDto.getId());
				camera.setGeoString(cameraDto.getGeoString());
				parseResult.put(camera.getId(), camera);
			}
		}
		return parseResult;
	}
}
