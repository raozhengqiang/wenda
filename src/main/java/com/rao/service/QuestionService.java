package com.rao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import com.rao.dao.QuestionDAO;
import com.rao.model.Question;

@Service
public class QuestionService {
	@Autowired
    QuestionDAO questionDAO;
	
	@Autowired
	SensitiveService sensitiveService;
	
	
	
	public int addQuestion(Question question) {
		question.setContent(HtmlUtils.htmlEscape(question.getContent()));
		question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
		questionDAO.addQuestion(question);
		//需要做敏感词过滤
		question.setContent(sensitiveService.filter(question.getContent()));
		question.setTitle(sensitiveService.filter(question.getTitle()));
		
		return questionDAO.addQuestion(question)>0?question.getId():0;
	}

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }
    
    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
    
    public Question getById(int id) {
        return questionDAO.getById(id);
    }
}