package com.rao.async;
//用来处理队列中的活动

import java.util.List;

public interface EventHandler {
	void doHandle(EventModel model);
	
	List<EventType> getSupportEventTypes();
}
