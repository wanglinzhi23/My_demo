package intellif.dto;

import intellif.database.entity.BlackDetail;
import intellif.database.entity.ImageInfo;
import intellif.database.entity.PersonDetail;

public class ZipUploadDetail {
	private ImageInfo imageInfo;
	private PersonDetail personDetail;
	private BlackDetail blackDetail;

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}

	public PersonDetail getPersonDetail() {
		return personDetail;
	}

	public void setPersonDetail(PersonDetail personDetail) {
		this.personDetail = personDetail;
	}

	public BlackDetail getBlackDetail() {
		return blackDetail;
	}

	public void setBlackDetail(BlackDetail blackDetail) {
		this.blackDetail = blackDetail;
	}
}
