package com.rao.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.rao.model.Comment;
import com.rao.model.EntityType;
import com.rao.model.HostHolder;
import com.rao.model.Question;
import com.rao.model.ViewObject;
import com.rao.service.CommentService;
import com.rao.service.LikeService;
import com.rao.service.QuestionService;
import com.rao.service.UserService;
import com.rao.utils.WendaUtil;

@Controller
public class QuestionController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LikeService likeService;
	
	@RequestMapping(value="/question/add",method= {RequestMethod.POST})
	@ResponseBody
	public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content) {
		try {
			Question question=new Question();
			question.setContent(content);
			question.setCreatedDate(new Date());
			question.setTitle(title);
			//判断有没有登陆，若没有登录则给一个匿名用户
			if(hostHolder.getUser()==null) {
				//question.setUserId(WendaUtil.ANONYMOUS_USERID);
				return WendaUtil.getJSONString(999);
			}else {
				question.setUserId(hostHolder.getUser().getId());
			}
			
			if(questionService.addQuestion(question)>0) {
				return WendaUtil.getJSONString(0);
			}
			
			
		} catch (Exception e) {
			logger.error("增加题目失败"+e.getMessage());
		}
		return WendaUtil.getJSONString(1, "失败");
	}
	
	@RequestMapping(value="/question/{qid}")
	public String questionDetail(Model model,@PathVariable("qid") int qid) {
		Question question=questionService.getById(qid);
		model.addAttribute("question", question);
		
		
		List<Comment> commentList=commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
		List<ViewObject> comments=new ArrayList<>();
		for (Comment comment : commentList) {
			ViewObject vo=new ViewObject();
			vo.set("comment", comment);
			if(hostHolder.getUser()==null) {
				vo.set("liked", 0);
			}else {
				vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
			}
			System.out.println(likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
			System.out.println(likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
			vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
			vo.set("user", userService.getUser(comment.getUserId()));
			comments.add(vo);
			
		}
		model.addAttribute("comments", comments);
		return "detail";
	}
	
	
	
	
	
}
