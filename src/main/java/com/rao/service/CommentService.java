package com.rao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.rao.dao.CommentDAO;
import com.rao.model.Comment;

@Service
public class CommentService {
	@Autowired
	CommentDAO commentDao;
	
	@Autowired
	SensitiveService sensitiveService;
	
	public List<Comment> getCommentsByEntity(int entityId,int entityType){
		return commentDao.selectCommnetByEntity(entityId, entityType);
	}
	
	public int addComment(Comment comment) {
		comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
		comment.setContent(sensitiveService.filter(comment.getContent()));
		return commentDao.addComment(comment)>0?comment.getId():0;
	}
	
	public int getCommentCount(int entityId,int entityType) {
		return commentDao.getCommentCount(entityId, entityType);
	}
	
	public void deleteComment(int entityId, int entityType) {
	    commentDao.updateStatus(entityId, entityType, 1);
	}
	
	public Comment getCommentById(int id) {
		return commentDao.getCommentById(id);
	}
	public int getUserCommentCount(int userId) {
        return commentDao.getUserCommentCount(userId);
    }
}
