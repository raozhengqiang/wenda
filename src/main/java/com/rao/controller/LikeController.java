package com.rao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rao.async.EventModel;
import com.rao.async.EventProducer;
import com.rao.async.EventType;
import com.rao.model.Comment;
import com.rao.model.EntityType;
import com.rao.model.HostHolder;
import com.rao.service.CommentService;
import com.rao.service.LikeService;
import com.rao.utils.WendaUtil;

@Controller
public class LikeController {//点赞业务
	@Autowired
	LikeService likeService;
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	EventProducer eventProducer;
	
	@Autowired
	CommentService commentService;
	
	@RequestMapping(path= {"/like"},method= {RequestMethod.POST})
	@ResponseBody
	public String like(@RequestParam("commentId") int commentId) {
		if(hostHolder.getUser()==null) {
			return WendaUtil.getJSONString(999);
		}
		Comment comment=commentService.getCommentById(commentId);
		//当有人点赞之后，会创建一个eventModel
		eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId()).setEntityId(commentId).setEntityId(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId()).setExt("questionId", String.valueOf(comment.getEntityId())));
		long likeCount=likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		System.out.println("数量为:"+likeCount);
		return WendaUtil.getJSONString(0,String.valueOf(likeCount));
	}
	
	@RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
						
}
