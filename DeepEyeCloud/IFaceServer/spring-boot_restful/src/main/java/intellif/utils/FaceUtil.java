package intellif.utils;

import intellif.consts.GlobalConsts;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FaceUtil {

	private static final Logger LOG = LogManager.getLogger(FaceUtil.class);

	public static final String DEFAULT_CHARSET = "UTF-8";

	public static final String DEFAULT_PATH = new File(FaceUtil.class.getResource("/").getPath()).getParent()
			+ "/output/";

	
	private static float javaVerify(float[] x, float[] y) {
		if (null == x || null == y) {
			throw new IllegalArgumentException("x is null or y is null");
		}
		if (x.length != y.length) {
			throw new IllegalArgumentException("x.length is " + x.length + ", y.length is " + y.length);
		}
		float f = javaVerify(x, y, 0, GlobalConsts.REAL_LENGTH_181);
		System.out.println("*******************!!!"+f);
		//return javaVerify(x, y, 0, GlobalConsts.REAL_LENGTH_181);
		return f;
	}

	private static boolean isLike(float[] x, float[] y) {
		float ret = 0;
		for (int i = 1; i < 129; i++) {
			ret += x[i] * x[i] + y[i] * y[i] - 4 * x[i] * y[i];
		}
		float retTemp = (float) (1 / (1 + Math.exp(ret)));
		return Float.compare(retTemp, 0.6f) >= 0;
	}

	public static float isLike(float[] x, float[] y, float threshold) {
		if (null == x || null == y || x.length < GlobalConsts.REAL_LENGTH_181 || y.length < GlobalConsts.REAL_LENGTH_181) {
			return 0;
		}
	
		//return Float.compare(javaVerify(x, y), threshold) >= 0;
		return javaVerify(x, y);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param n
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
	 * @param index
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
	 * 自增
	 * 
	 * @param fb
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
	 * @param fb
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






	
}
