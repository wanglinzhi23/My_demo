package org.lihao.demo.api;

import org.lihao.demo.entity.FaceInfo;

import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: FaceInfoService.java
 * @Package org.lihao.demo.api
 * @Description Face Information Service
 * @date 2018 04-09 19:41.
 */
public interface FaceInfoService {
	int insert(FaceInfo faceInfo);

	int delete(Long id);

	int update(FaceInfo faceInfo);

	List<FaceInfo> listAll();

	FaceInfo getById(Long id);
}
