package intellif.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import intellif.consts.GlobalConsts;
import intellif.dao.TableRecordDao;
import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.service.ImageServiceItf;
import intellif.utils.DateUtil;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.TableRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ImageServiceImpl implements ImageServiceItf {

    @Autowired
    private ImageInfoDaoImpl imageInfoDaoImpl;
    @Autowired
	TableRecordDao tableRecordDao;

	@Override
	public Long countMinute() {
		
		long rCount = 0;
		TableRecord table = tableRecordDao.getCurTable(DateUtil.getDateString(new Date()), GlobalConsts.T_IMAGE_PRE);
		String sql = "select max(sequence) from "+GlobalConsts.INTELLIF_FACE+"."+table.getTableName();
		long maxSequence = imageInfoDaoImpl.countResult(sql);
	    if(maxSequence - GlobalConsts.imageSequence > 0 && GlobalConsts.imageSequence != 0){
			rCount = maxSequence - GlobalConsts.imageSequence;
		}
	    GlobalConsts.imageSequence = maxSequence;
		return rCount;
	}
		
	@Override
	public Long countDay() {
		
		String selectString = "count(1)";
		String queryString = "";
		Date now = new Date();
		String startTime = DateUtil.getDateString(now);
		String endTime = DateUtil.getDateString(new Date(now.getTime()+24*3600*1000));
		String timeField = "time";
		int page = 1;
		int pageSize = Integer.MAX_VALUE;
		List<BigInteger> count = imageInfoDaoImpl.findByTime(selectString, startTime, endTime, timeField, queryString, BigInteger.class, page, pageSize, false,null);
		if(count!=null&&count.size()!=0){
			return count.get(0).longValue();
		}else{
		    return 0L;
		}		
	}
		
	@Override
	public Long count() {
		Long count = imageInfoDaoImpl.count();
		return count;
	}

	@Override
	public ImageInfo findById(long image_id) {

		List<Long> id = new ArrayList<Long>();
		id.add(image_id);
		List<ImageInfo> imageList = imageInfoDaoImpl.findByIds(id);
		if(null != imageList && !imageList.isEmpty()) {
    		return imageList.get(0);
		} else {
			return null;
		}	
	}



	@Override
	public ImageInfo save(ImageInfo imageinfo) {
		return imageInfoDaoImpl.save(imageinfo);
	}

	@Override
	public int update(ImageInfo imageinfo) {
		
		return 0;
	}

	/*@Override
	public String delete(Long id) {
		
		return null;
		
	}*/

	
}
