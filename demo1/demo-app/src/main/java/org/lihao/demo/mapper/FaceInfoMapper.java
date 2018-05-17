package org.lihao.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.lihao.demo.entity.FaceInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: FaceInfoMapper.java
 * @Package org.lihao.demo.mapper
 * @Description Face Information Mapper
 * @date 2018 04-09 19:31.
 */
@Mapper
@Repository
public interface FaceInfoMapper {

	@Select("select * from bs_t_face_info where id=#{id} limit 1")
	FaceInfo loadOneByAnn(@Param("id") Long id);

	int insert(FaceInfo faceInfo);

	int delete(@Param("id") Long id);

	int update(FaceInfo faceInfo);

	List<FaceInfo> listAll();

	FaceInfo getById(@Param("id") Long id);
}
