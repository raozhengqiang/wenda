package com.rao.async2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.rao.async2.EventConsumer;
import com.rao.utils.JedisAdapter;
import com.rao.utils.RedisKeyUtil;


@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
	private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
	private Map<EventType, List<EventHandler>> config=new HashMap<>();
	private ApplicationContext applicationContext;
	
	@Autowired
	JedisAdapter jedisAdapter;

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
		if(beans!=null) {
			for ( Map.Entry<String, EventHandler> entry:beans.entrySet()) {
				List<EventType> eventTypes=entry.getValue().getSupportEventType();
				for(EventType type:eventTypes) {
					if (!config.containsKey(type)){
						config.put(type, new ArrayList<EventHandler>());
					}
					config.get(type).add(entry.getValue());
				}
			}
		}
		//启动线程去队列中寻找
		Thread thread=new Thread(new Runnable() {
			
			@Override
			public void run() {
				String key=RedisKeyUtil.getEventQueueKey();
				List<String> events=jedisAdapter.brpop(0, key);
				for(String message:events) {
					if(message.equals(key)) {
						continue;
					}
					EventModel model=JSON.parseObject(message,EventModel.class);
					if(!config.containsKey(model.getType())) {
						logger.error("不能识别事件类型");
						continue;
					}
					//找出所有对应的状态进行处理
					for(EventHandler eventHandler:config.get(model.getType())) {
						eventHandler.doHandle(model);
					}
				}
				
			}
		});
		thread.start();
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
		
	}
	
}
