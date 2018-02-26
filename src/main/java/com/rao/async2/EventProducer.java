package com.rao.async2;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.rao.utils.JedisAdapter;
import com.rao.utils.RedisKeyUtil;

public class EventProducer {
	
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
