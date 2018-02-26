package com.rao.async;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.rao.controller.HomeController;
import com.rao.utils.JedisAdapter;
import com.rao.utils.RedisKeyUtil;
//InitializingBean为初始化实现类
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
    	//spring启动后，可以将所有继承EventHandler的类全部找出来
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();//将这些处理器全部取出来，然后遍历每一个handler，将他们支持的时间类型全部找出来
                //将这些事件先注册起来
                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                    	//若config容器中没有这个任务类型，则创建一个新的map
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    
                    config.get(type).add(entry.getValue());
                }
            }
        }
        //启动线程，去寻找队列中的事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            	//写一个循环一直去获取队列
                while(true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    //弹出最后一个元素，找到相关的handler处理
                    List<String> events = jedisAdapter.brpop(0, key);

                    for (String message : events) {
                        if (message.equals(key)) {
                            continue;
                        }
                        //通过反序列化将字符串转为对象
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }

                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
        
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
