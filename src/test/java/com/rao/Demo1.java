package com.rao;

import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.rao.dao.QuestionDAO;
import com.rao.dao.UserDao;
import com.rao.model.Question;
import com.rao.model.User;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Demo1 {
	@Autowired
	UserDao userDao;
	@Autowired
	QuestionDAO questionDao;
	
	
	//@Test
	public void testAddUser() {
		Random random = new Random();
        for (int i = 0; i < 11; ++i) {
        	/*
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);
            
            user.setPassword("xx");
            userDao.updatePassword(user);*/
            
            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setUserId(i + 1);
            question.setTitle(String.format("TITLE{%d}", i));
            question.setContent(String.format("Balaababalalalal Content %d", i));
            questionDao.addQuestion(question);
            
            
        }
        //Assert.assertEquals("xx", userDao.selectById(1).getPassword());
        //userDao.deleteById(1);
        //Assert.assertNull(userDao.selectById(1));
        
        //System.out.println(questionDao.selectLatestQuestions(0, 0, 10));
	}
}

