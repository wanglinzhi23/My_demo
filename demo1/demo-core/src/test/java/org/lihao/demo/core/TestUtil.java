package org.lihao.demo.core;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.lihao.demo.core.util.NameUtil;

/**
 * @author Lihao
 * @version V1.0
 * @Title: TestUtil.java
 * @Package org.lihao.demo
 * @Description
 * @date 2018 04-09 9:57.
 */
public class TestUtil {
	@Test
	public void getRandomNameTest(){
		//System.out.println(NameUtil.getRandomName());
		Assertions.assertThat(NameUtil.getRandomName()!=null);
	}
}
