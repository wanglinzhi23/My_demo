package intellif.service;

import java.util.List;

import intellif.database.entity.ImageInfo;


public interface ImageServiceItf  {
		
	/**
	 * 获取指定id的图片
	 * @param id
	 * @return
	 */
    ImageInfo findById(long image_id);
    
    
    /**
	 * 统计所有图片数量
	 * @return
	 */
    Long count();
    
    /**
	 * 统计最近一分钟的图片数量
	 * @return
	 */
    Long countMinute();
    
    /**
	 * 统计最近一天的图片数量
	 * @return
	 */
    Long countDay();
    
    /**
	 * 创建或更新对象（根据id判断是否为新对象）
	 * @param image
	 * @return
	 */
    ImageInfo save(ImageInfo imageinfo);
    
    int update(ImageInfo imageinfo);
    
 //   String delete(Long id);
}

