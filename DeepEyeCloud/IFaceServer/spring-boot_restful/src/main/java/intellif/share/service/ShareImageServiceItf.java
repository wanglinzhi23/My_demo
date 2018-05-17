package intellif.share.service;

import intellif.database.entity.ImageInfo;

public interface ShareImageServiceItf {
	/**
	 * 获取指定id的图片
	 * @param id
	 * @return
	 */
    ImageInfo findById(long image_id);
}
