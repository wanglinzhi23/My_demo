package org.lihao.demo;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lihao.demo.entity.FaceInfo;
import org.lihao.demo.mapper.FaceInfoMapper;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

import java.util.Date;
import java.util.List;

/**
 * @author Lihao
 * @version V1.0
 * @Title: TestFaceInfoMapper.java
 * @Package org.lihao.demo
 * @Description Test FaceInfoMapper
 * @date 2018 04-09 20:12.
 */
@MybatisTest//是sing
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)//如果开启了Swagger则需要 在这个启动类上增加//@EnableWebMvc 这是方法2
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestFaceInfoMapper {

	/**/
	//方法1为在测试用例中增加如下两段代码
	@MockBean
	DocumentationPluginsBootstrapper mock; //keep swagger happy
	@MockBean
	WebMvcRequestHandlerProvider another;  //keep swagger happy


	@Autowired
	private FaceInfoMapper faceInfoMapper;

	@Test
	public void loadOneByAnnTest(){
		FaceInfo faceInfo = faceInfoMapper.loadOneByAnn(1L);
		Assertions.assertThat(faceInfo!=null);
	}

	@Test
	public void insertTest(){
		FaceInfo faceInfo = new FaceInfo();
		faceInfo.setAge(29);
		faceInfo.setGender(1);
		faceInfo.setFileName("a0d575c53a89288c.jpg");
		faceInfo.setPath("http://img2.woyaogexing.com/2018/03/27/a0d575c53a89288c!400x400_big.jpg");
		faceInfo.setCreateTime(new Date());
		int count = faceInfoMapper.insert(faceInfo);
		Assertions.assertThat(count>0);
	}

	@Test
	public void deleteTest(){
		int count = faceInfoMapper.delete(1L);
		Assertions.assertThat(count>0);
	}

	@Test
	public void updateTest(){
		FaceInfo faceInfo = new FaceInfo();
		faceInfo.setId(1L);
		faceInfo.setAge(23);
		faceInfo.setGender(2);
		faceInfo.setFileName("a0d575c53a89288c1.jpg");
		faceInfo.setPath("http://img2.woyaogexing.com/2018/03/27/a0d575c53a89288c!400x400_big.jpg");
		faceInfo.setCreateTime(new Date());
		int count = faceInfoMapper.update(faceInfo);
		Assertions.assertThat(count>0);
	}

	@Test
	public void listAllTest(){
		List<FaceInfo> faceInfoList = faceInfoMapper.listAll();
		Assertions.assertThat(faceInfoList.size()>0);
	}

	@Test
	public void getByIdTest(){
		FaceInfo faceInfo = faceInfoMapper.getById(1L);
		Assertions.assertThat(faceInfo!=null);
	}
}
