package com.rao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rao.service.LikeService;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class LikeServiceTest {
	@Autowired
	LikeService likeService;
	
	@Before//执行单元测试前需要准备的数据
	public void setUp() {
		System.out.println("setUp");
	}
	
	@After//测试完之后需要清理表
	public void tearDown() {
		System.out.println("tearDown");
	}
	@Test
	public void testLike() {
		System.out.println("testLike");
		likeService.like(123, 1, 1);
		Assert.assertEquals(1, likeService.getLikeStatus(123, 1, 1));
		
		likeService.disLike(123, 1, 1);
        Assert.assertEquals(-1, likeService.getLikeStatus(123, 1, 1));
	}
}
