package intellif.service;

public interface PoliceManAuthorityServiceItf {

	public boolean batchDelete(int switchType, String policeNoLine);
	
	public void addIfNotExsit(String policeno,int authType);	
	
	public void deleteIfExsit(String policeno,int authType);	

}
