package com.rao.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.rao.utils.JedisAdapter;
import com.rao.utils.RedisKeyUtil;

@Service
public class EventProducer {//制造事件,放入一个队列中，这个队列由redis产生
	@Autowired
	JedisAdapter jedisAdapter;
	
	public boolean fireEvent(EventModel eventModel) {
		try {
			String json=JSONObject.toJSONString(eventModel);
			String key=RedisKeyUtil.getEventQueueKey();
			jedisAdapter.lpush(key, json);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
