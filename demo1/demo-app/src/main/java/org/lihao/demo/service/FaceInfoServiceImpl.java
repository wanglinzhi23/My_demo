package org.lihao.demo.service;

import org.lihao.demo.api.FaceInfoService;
import org.lihao.demo.entity.FaceInfo;
import org.lihao.demo.mapper.FaceInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: FaceInfoServiceImpl.java
 * @Package org.lihao.demo.service
 * @Description Face Information Service
 * @date 2018 04-09 19:44.
 */
@Service
public class FaceInfoServiceImpl implements FaceInfoService {

	@Autowired
	private FaceInfoMapper faceInfoMapper;

	@Override
	public int insert(FaceInfo faceInfo) {
		return faceInfoMapper.insert(faceInfo);
	}

	@Override
	public int delete(Long id) {
		return faceInfoMapper.delete(id);
	}

	@Override
	public int update(FaceInfo faceInfo) {
		return faceInfoMapper.update(faceInfo);
	}

	@Override
	public List<FaceInfo> listAll() {
		return faceInfoMapper.listAll();
	}

	@Override
	public FaceInfo getById(Long id) {
		return faceInfoMapper.getById(id);
	}
}
