package com.rao.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rao.dao.FeedDAO;
import com.rao.model.Feed;

@Service
public class FeedService {
	@Autowired
	FeedDAO feedDao;
	
	//拉模式，找出所有相关的人
	public List<Feed> getUserFeeds(int maxId,List<Integer> userIds,int count){
		return feedDao.selectUserFeeds(maxId, userIds, count);
	}
	
	public boolean addFeed(Feed feed) {
		feedDao.addFeed(feed);
		return feed.getId()>0;
	}
	
	//推模式，向好友推送自己的状态
	public Feed getById(int id) {
		return feedDao.getFeedById(id);
	}
}
