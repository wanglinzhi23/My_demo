package intellif.lire;

import intellif.utils.IFaceSDKUtil;
import intellif.utils.SerializeUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcherFactory;
import net.semanticmetadata.lire.impl.GenericFastImageSearcher;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

/**
 * Simple image retrieval with Lire
 * @author Mathias Lux, mathias <at> juggle <dot> at
 */
public class Searcher {
    public static void main(String[] args) throws IOException {
		System.out.println("初始化... 1");
		System.load("C:\\facesdk.dll");
		System.out.println("初始化... 2");
		System.load("C:\\faceverify.dll");
		System.out.println("初始化... 3");
		System.load("C:\\IFaceSDK.dll");
		System.out.println("初始化... 4");
		IFaceSDKUtil.getInstance();
		System.out.println("初始化... 5");
//		 System.exit(0);
//		ApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] {"com/service/spring_service.xml"});
//		CollectServiceItf collectServiceItf = (CollectServiceItf) appContext.getBean("collectServiceItf");
//		List<String> condition = new ArrayList<String>();
//		condition.add("id,=,2");
//		List<FaceInfo> faceList = collectServiceItf.getFaceInfoByCondition(condition);
//		System.out.println("人脸数量:"+faceList.size());
		byte[] fdata = new byte[724];
		List face = (List) IFaceSDKUtil.detect("C:\\Users\\Administrator\\Desktop\\temp.jpg").get(0);
		for(int i = 0;i<181;i++) {
			byte[] tempbyte = SerializeUtil.float2byte(((float[])face.get(0))[i]);
			System.arraycopy(tempbyte,0*i,fdata,4*i,tempbyte.length);
		}
		
        Date now = new Date();
    	
    	 IndexReader ir = DirectoryReader.open(FSDirectory.open(new File("lireIndex")));
         GenericFastImageSearcher searcher = (GenericFastImageSearcher) ImageSearcherFactory.createIFACEFImageSearcher(20);
  
//         ImageSearchHits hits = searcher.search(faceList.get(0).getFaceFeature(), ir);
         ImageSearchHits hits = searcher.search(fdata, ir, 0, new HashMap());
         for (int i = 0; i < hits.length(); i++) {
             String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
             System.out.println(hits.score(i) + ": \t" + fileName);
         }
         System.out.println("cost time:"+(new Date().getTime()-now.getTime()));
    	
    }
}