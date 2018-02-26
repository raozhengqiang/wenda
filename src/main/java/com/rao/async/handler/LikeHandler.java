package com.rao.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rao.async.EventHandler;
import com.rao.async.EventModel;
import com.rao.async.EventType;
import com.rao.model.Message;
import com.rao.model.User;
import com.rao.service.MessageService;
import com.rao.service.UserService;
import com.rao.utils.WendaUtil;

@Component
public class LikeHandler implements EventHandler{
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;
	
	@Override
	public void doHandle(EventModel model) {
		Message message=new Message();
		message.setFromId(WendaUtil.SYSTEM_USERID);//设置管理员的来信
		message.setToId(model.getEntityOwnerId());
		message.setCreatedDate(new Date());
		User user=userService.getUser(model.getActorId());//找到事件发起人，即评论者
		message.setContent("用户"+user.getName()+"赞你评论的人,http://127.0.0.1:8080/question/"+model.getExt("questionId"));
		messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		return Arrays.asList(EventType.LIKE);
	}

}
