package com.rao.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.rao.model.Comment;
import com.rao.model.EntityType;
import com.rao.model.HostHolder;
import com.rao.model.Message;
import com.rao.model.User;
import com.rao.model.ViewObject;
import com.rao.service.CommentService;
import com.rao.service.MessageService;
import com.rao.service.QuestionService;
import com.rao.service.SensitiveService;
import com.rao.service.UserService;
import com.rao.utils.WendaUtil;

@Controller
public class MessageController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	SensitiveService sensitiveService;
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	MessageService messageService;
	
	//增加一条评论
	@RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
	@ResponseBody
	public String addMessage(@RequestParam("toName") String toName,
	                         @RequestParam("content") String content) {
	    try {
	        if(hostHolder.getUser()==null) {
	        	return WendaUtil.getJSONString(999, "未登录");
	        }
	        User user=userService.selectByName(toName);
	        
	        if(user==null) {
	        	return WendaUtil.getJSONString(1, "用户不存在");
	        }
	        	
	        Message msg=new Message();
	        msg.setContent(content);
	        msg.setFromId(hostHolder.getUser().getId());
	        msg.setToId(user.getId());
	        msg.setCreatedDate(new Date());
	        int a=messageService.addMessage(msg);
	        System.out.println(a);
	        return WendaUtil.getJSONString(0);
				
		} catch (Exception e) {
			logger.error("发送消息失败"+e.getMessage());
			return WendaUtil.getJSONString(1, "发送消息失败");
		}
	}
	
	@RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
	public String getConversationList(Model model) {
		try {
			if(hostHolder.getUser()==null) {
				return "redirect:/reglogin";
			}
			int localUserId=hostHolder.getUser().getId();
			List<ViewObject> conversations=new ArrayList<>();
			List<Message> conversationList=messageService.getConversationList(localUserId, 0, 10);
			for (Message msg : conversationList) {
				ViewObject vo=new ViewObject();
				vo.set("conversation", msg);
				int targetId=msg.getFromId()==localUserId?msg.getToId():msg.getFromId();
				User user=userService.getUser(targetId);
				vo.set("user", user);
				vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
				conversations.add(vo);
			}
			model.addAttribute("conversations", conversations);
		} catch (Exception e) {
			logger.error("获取站内信列表失败"+e.getMessage());
		}
		
		return "letter";
	}
	
	@RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
	public String conversationDetail(Model model,@Param("conversationId") String conversationId) {
		try {
			List<Message> conversationList=messageService.getConversationDetail(conversationId, 0, 10);
			List<ViewObject> messages=new ArrayList<>();
			for (Message msg : conversationList) {
				ViewObject vo=new ViewObject();
				vo.set("message", msg);
				User user=userService.getUser(msg.getFromId());
				if(user==null) {
					continue;
				}
				vo.set("headUrl", user.getHeadUrl());
				vo.set("userId", user.getId());
				messages.add(vo);
			}
			model.addAttribute("messages", messages);
		} catch (Exception e) {
			logger.error("获取详情失败"+e.getMessage());
		}
		return "letterDetail";
	}
	
	
	
	
	
	
	
	
	
	
	
}
