package intellif.service;

import intellif.database.entity.BlackBank;
import intellif.database.entity.BlackDetail;

import java.util.List;
import java.util.Map;



/**
 * The Interface BlackDetailServiceItf.
 */
public interface BlackDetailServiceItf<T> extends CommonServiceItf<T> {
    /**
     * 
     * @param blackDetail
     * @param copyUrl ͼƬ�����Դ
     * @return
     * @throws Exception
     */
	boolean updateFaceFeature(BlackDetail blackDetail,String copyUrl) throws Exception;
	Map<Long, List<BlackDetail>> getBlackByBanksPage(List<BlackBank> bankList,int size);
	public List<BlackDetail> getBlackDetailsByPerson(long personId);
}
