package intellif.utils;

public class GetHashCode {
	private long time;
	private String policeId;
	private String faceFature;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((faceFature == null) ? 0 : faceFature.hashCode());
		result = prime * result + ((policeId == null) ? 0 : policeId.hashCode());
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GetHashCode other = (GetHashCode) obj;
		if (faceFature == null) {
			if (other.faceFature != null)
				return false;
		} else if (!faceFature.equals(other.faceFature))
			return false;
		if (policeId == null) {
			if (other.policeId != null)
				return false;
		} else if (!policeId.equals(other.policeId))
			return false;
		if (time != other.time)
			return false;
		return true;
	}
	
	 public GetHashCode(long time,String policeId,String faceFature) {
		 this.policeId = policeId;
		 this.time = time;
		 this.faceFature = faceFature;
	}
	 
}
