package com.rao.async2;

import java.util.List;

public interface EventHandler {
	void doHandle(EventModel model);
	List<EventType> getSupportEventType();
	
}
