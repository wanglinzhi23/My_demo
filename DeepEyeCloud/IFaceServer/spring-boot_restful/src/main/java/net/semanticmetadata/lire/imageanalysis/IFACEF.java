/*
 * This file is part of the LIRE project: http://www.semanticmetadata.net/lire
 * LIRE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * LIRE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LIRE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * We kindly ask you to refer the any or one of the following publications in
 * any publication mentioning or employing Lire:
 *
 * Lux Mathias, Savvas A. Chatzichristofis. Lire: Lucene Image Retrieval 鈥?
 * An Extensible Java CBIR Library. In proceedings of the 16th ACM International
 * Conference on Multimedia, pp. 1085-1088, Vancouver, Canada, 2008
 * URL: http://doi.acm.org/10.1145/1459359.1459577
 *
 * Lux Mathias. Content Based Image Retrieval with LIRE. In proceedings of the
 * 19th ACM International Conference on Multimedia, pp. 735-738, Scottsdale,
 * Arizona, USA, 2011
 * URL: http://dl.acm.org/citation.cfm?id=2072432
 *
 * Mathias Lux, Oge Marques. Visual Information Retrieval using Java and LIRE
 * Morgan & Claypool, 2013
 * URL: http://www.morganclaypool.com/doi/abs/10.2200/S00468ED1V01Y201301ICR025
 *
 * Copyright statement:
 * ====================
 * (c) 2002-2013 by Mathias Lux (mathias@juggle.at)
 *  http://www.semanticmetadata.net/lire, http://www.lire-project.net
 *
 * Updated: 16.01.15 10:26
 */

package net.semanticmetadata.lire.imageanalysis;

import intellifusion.IFaceSDKUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.utils.SerializationUtils;


/**
 * The CEDD feature was created, implemented and provided by Savvas A. Chatzichristofis<br/>
 * More information can be found in: Savvas A. Chatzichristofis and Yiannis S. Boutalis,
 * <i>CEDD: Color and Edge Directivity Descriptor. A Compact
 * Descriptor for Image Indexing and Retrieval</i>, A. Gasteratos, M. Vincze, and J.K.
 * Tsotsos (Eds.): ICVS 2008, LNCS 5008, pp. 312-322, 2008.
 *
 * @author: Savvas A. Chatzichristofis, savvash@gmail.com
 */
public class IFACEF implements LireFeature {
    private double T0;
    private double T1;
    private double T2;
    private double T3;
    private boolean Compact = false;
    protected float[] data = new float[181];
    protected byte[] histogram = new byte[724];

    int tmp;
    // for tanimoto:
    private double Result, Temp1, Temp2, TempCount1, TempCount2, TempCount3;
    private IFACEF tmpFeature;
    private double iTmp1, iTmp2;


    public IFACEF(double Th0, double Th1, double Th2, double Th3, boolean CompactDescriptor) {
        this.T0 = Th0;
        this.T1 = Th1;
        this.T2 = Th2;
        this.T3 = Th3;
        this.Compact = CompactDescriptor;
    }

    public IFACEF() {
        this.T0 = 14d;
        this.T1 = 0.68d;
        this.T2 = 0.98d;
        this.T3 = 0.98d;
    }

    // Apply filter
    // signature changed by mlux
    public void extract(BufferedImage image) {
//        double qCEDD[];
//        int max=127;
//        int min=1;
//        Random random = new Random();
//        
//        File file2 = new File("test"+random.nextInt(1000000)+".jpg");
//        try {
//			ImageIO.write(image, "jpg", file2);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//       List face = IFaceSDKUtil.detect(file2.getAbsolutePath());
//        
//        if (Compact == false) {
//        	data = (float[]) ((List) face.get(0)).get(0);
//        } else {
//        	data = (float[]) ((List) face.get(0)).get(0);
//        }
//        file2.delete();
    }

    public float getDistance(LireFeature vd) {
        // Check if instance of the right class ...
//        if (!(vd instanceof IFACEF))
//            throw new UnsupportedOperationException("Wrong descriptor.");

        // casting ...
        IFACEF tmpFeature = (IFACEF) vd;

        // check if parameters are fitting ...
        if ((tmpFeature.data.length != data.length))
            throw new UnsupportedOperationException("Data lengths do not match");

//        for (int i = 0; i < data.length; i++) {
//            data[i] = byte2float(histogram, 4*i);
//            tmpFeature.data[i] = byte2float(tmpFeature.histogram, 4*i);
//        }

//        float mResult = (float) IFaceSDKUtil.verify(data, tmpFeature.data);

        float mResult = javaVerify(data, tmpFeature.data, data.length);
        return mResult;
    }	
    
    public static float javaVerify(float[] x, float[] y, int n) {
		float ret = 0;
		for(int i=0; i<n; i++){
			ret += x[i]*x[i] + y[i]*y[i] - 4*x[i]*y[i];
		}
		float retTemp = (float) (1/(1+Math.exp(ret)));
		if(retTemp<=0.5) {
			ret = 3.3342F*retTemp-0.7671F;
		} else {
			ret = 0.2F*retTemp+0.8F;
		}
		return ret;
	}

    @SuppressWarnings("unused")
    private double scalarMult(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    public byte[] getByteHistogram() {
        return histogram;
    }

    public String getStringRepresentation() { // added by mlux
        StringBuilder sb = new StringBuilder(histogram.length * 2 + 25);
        sb.append("cedd");
        sb.append(' ');
        sb.append(histogram.length);
        sb.append(' ');
        for (byte aData : histogram) {
            sb.append((int) aData);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public void setStringRepresentation(String s) { // added by mlux
        StringTokenizer st = new StringTokenizer(s);
        if (!st.nextToken().equals("cedd"))
            throw new UnsupportedOperationException("This is not a CEDD descriptor.");
        for (int i = 0; i < histogram.length; i++) {
            if (!st.hasMoreTokens())
                throw new IndexOutOfBoundsException("Too few numbers in string representation.");
            histogram[i] = (byte) Integer.parseInt(st.nextToken());
        }

    }

    /**
     * Provides a much faster way of serialization.
     *
     * @return a byte array that can be read with the corresponding method.
     * @see net.semanticmetadata.lire.imageanalysis.IFACEF#setByteArrayRepresentation(byte[])
     */
    public byte[] getByteArrayRepresentation() {
        // find out the position of the beginning of the trailing zeros.
//        byte[] result = new byte[181*4];
//        
//        for(int i =0;i<data.length;i++) {
//			byte[] fdata = float2byte(data[i]);
//			System.arraycopy(fdata,0*i,result,4*i,fdata.length);
//		}

        return histogram;
    }
    
    public static byte[] float2byte(float f) {  
        // 把float转换为byte[]  
        int fbit = Float.floatToIntBits(f);  
         
        byte[] b = new byte[4];    
        for (int i = 0; i < 4; i++) {    
            b[i] = (byte) (fbit >> (24 - i * 8));    
        }   
          
        // 翻转数组  
        int len = b.length;  
        // 建立一个与源数组元素类型相同的数组  
        byte[] dest = new byte[len];  
        // 为了防止修改源数组，将源数组拷贝一份副本  
        System.arraycopy(b, 0, dest, 0, len);  
        byte temp;  
        // 将顺位第i个与倒数第i个交换  
        for (int i = 0; i < len / 2; ++i) {  
            temp = dest[i];  
            dest[i] = dest[len - i - 1];  
            dest[len - i - 1] = temp;  
        }  
        return dest;  
    }  

    /**
     * Reads descriptor from a byte array. Much faster than the String based method.
     *
     * @param in byte array from corresponding method
     * @see net.semanticmetadata.lire.imageanalysis.IFACEF#getByteArrayRepresentation
     */
    public void setByteArrayRepresentation(byte[] in) {
        setByteArrayRepresentation(in, 0, in.length);
    }

    public void setByteArrayRepresentation(byte[] in, int offset, int length) {
    	histogram = in;
        for (int i = 0; i < data.length; i++) {
            data[i] = byte2float(histogram, 4*i);
        }
    }
    /** 
     * 字节转换为浮点 
     *  
     * @param b 字节（至少4个字节） 
     * @param index 开始位置 
     * @return 
     */  
    public static float byte2float(byte[] b, int index) {    
        int l;                                             
        l = b[index + 0];                                  
        l &= 0xff;                                         
        l |= ((long) b[index + 1] << 8);                   
        l &= 0xffff;                                       
        l |= ((long) b[index + 2] << 16);                  
        l &= 0xffffff;                                     
        l |= ((long) b[index + 3] << 24);                  
        return Float.intBitsToFloat(l);                    
    }  

    public double[] getDoubleHistogram() {
        return SerializationUtils.castToDoubleArray(histogram);
    }

    @Override
    public String getFeatureName() {
        return "IFACEF";
    }

    @Override
    public String getFieldName() {
        return DocumentBuilder.FIELD_NAME_IFACEF;
    }
}
