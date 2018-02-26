package com.rao.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import com.rao.model.Comment;
import com.rao.model.EntityType;
import com.rao.model.HostHolder;
import com.rao.service.CommentService;
import com.rao.service.QuestionService;
import com.rao.service.SensitiveService;
import com.rao.utils.WendaUtil;

@Controller
public class CommentController {
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	SensitiveService sensitiveService;
	
	@Autowired
	QuestionService questionService;
	
	//增加一条评论
	 @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
	    public String addComment(@RequestParam("questionId") int questionId,
	                             @RequestParam("content") String content) {
	        try {
	            content = HtmlUtils.htmlEscape(content);
	            content = sensitiveService.filter(content);
	            // 过滤content
	            Comment comment = new Comment();
	            if (hostHolder.getUser() != null) {
	                comment.setUserId(hostHolder.getUser().getId());
	            } else {
	                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
	            }
	            comment.setContent(content);
	            comment.setEntityId(questionId);
	            comment.setEntityType(EntityType.ENTITY_QUESTION);
	            comment.setCreatedDate(new Date());
	            comment.setStatus(0);

	            commentService.addComment(comment);
	            
	            int count=commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
	            questionService.updateCommentCount(comment.getEntityId(), count);
	            
	            // 怎么异步化
	        } catch (Exception e) {
	            logger.error("增加评论失败" + e.getMessage());
	        }
	        return "redirect:/question/" + String.valueOf(questionId);
	    }
}
