package intellif.share.service.impl;

import intellif.dao.impl.ImageInfoDaoImpl;
import intellif.share.service.ShareImageServiceItf;
import intellif.database.entity.ImageInfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ShareImageServiceImpl implements ShareImageServiceItf {
	
	 @Autowired
	 private ImageInfoDaoImpl imageInfoDaoImpl;

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

}
