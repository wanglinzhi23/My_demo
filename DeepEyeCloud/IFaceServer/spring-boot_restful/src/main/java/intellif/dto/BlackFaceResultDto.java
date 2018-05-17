package intellif.dto;

public class BlackFaceResultDto extends FaceResultDto {
private long bankId;

public BlackFaceResultDto(FaceResultDto dto,long bankId){
	this.setBankId(bankId);
	this.setCamera(dto.getCamera());
	this.setFile(dto.getFile());
	this.setFile_bg(dto.getFile_bg());
	this.setId(dto.getId());
	this.setScore(dto.getScore());
	this.setTime(dto.getTime());
	this.setType(dto.getType());
}
public long getBankId() {
	return bankId;
}

public void setBankId(long bankId) {
	this.bankId = bankId;
}

}
