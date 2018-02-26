package com.rao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rao.dao.MessageDao;
import com.rao.model.Message;

@Service
public class MessageService {
	@Autowired
	MessageDao messageDao;
	
	@Autowired
	SensitiveService sensitiveService;
	
	public int addMessage(Message message) {
		message.setContent(sensitiveService.filter(message.getContent()));
		return messageDao.addMessage(message);
	}
	
	public List<Message> getConversationDetail(String conversationId,int offset,int limit){
		return messageDao.getConversationDetail(conversationId, offset, limit);
	}
	
	public List<Message> getConversationList(int userId,int offset,int limit){
		return messageDao.getConversationList(userId, offset, limit);
	}
	
	public int getConvesationUnreadCount(int userId,String conversationId) {
		return messageDao.getConvesationUnreadCount(userId, conversationId);
	}
	
	
}
