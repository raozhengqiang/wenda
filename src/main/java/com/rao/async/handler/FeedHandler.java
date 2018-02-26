package com.rao.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rao.async.EventHandler;
import com.rao.async.EventModel;
import com.rao.async.EventType;
import com.rao.model.EntityType;
import com.rao.model.Feed;
import com.rao.model.Question;
import com.rao.model.User;
import com.rao.service.FeedService;
import com.rao.service.FollowService;
import com.rao.service.QuestionService;
import com.rao.service.UserService;
import com.rao.utils.JedisAdapter;
import com.rao.utils.RedisKeyUtil;

@Component
public class FeedHandler implements EventHandler{
	@Autowired
	QuestionService questionService;
	@Autowired
	UserService userService;
	
	@Autowired
	FeedService feedServie;
	
	@Autowired
	FollowService followService;
	@Autowired
	JedisAdapter jedisAdapter;
	
	//获取评论的数据
	public String buildFeedData(EventModel model) {
		Map<String, String> map=new HashMap<>();
		//寻找触发的用户
		User actor=userService.getUser(model.getActorId());
		if(actor==null) {
			return null;
		}
		map.put("userId", String.valueOf(actor.getId()));
		map.put("userHead", actor.getHeadUrl());
		map.put("userName", actor.getName());
		
		if(model.getType()==EventType.COMMENT|| (model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
			Question question=questionService.getById(model.getEntityId());
			if(question==null) {
				return null;
			}
			map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
		}
		return null;
	}

	@Override
	public void doHandle(EventModel model) {
		//当有人评论时
		Feed feed=new Feed();
		feed.setCreatedDate(new Date());
		feed.setUserId(model.getActorId());
		feed.setType(model.getType().getValue());
		feed.setData(buildFeedData(model));
		if(feed.getData()==null) {
			return;
		}
		feedServie.addFeed(feed);
		
		//给事件的粉丝推
		List<Integer> followers=followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
		//系统队列
		followers.add(0);
		for(int follower:followers) {
			String timelineKey=RedisKeyUtil.getTimelineKey(follower);
			jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
		}
		
		
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		// TODO Auto-generated method stub
		return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});
	}

}
