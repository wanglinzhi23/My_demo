package intellif.chd.service;

import java.util.List;

import intellif.chd.vo.FilterFace;

public interface CacheService {
	

	List<FilterFace> filterFaceList();
	
	void saveFilterFace(FilterFace filterFace);
	
	void saveFilterFace(List<FilterFace> filterFaceList);
}
