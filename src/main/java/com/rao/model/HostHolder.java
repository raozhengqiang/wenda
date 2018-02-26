package com.rao.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
	//每个线程对变量都有一个拷贝，在底层相当于有一个Map(TereadId,user)
	private static ThreadLocal<User> users=new ThreadLocal<>();
	
	public User getUser() {
		return users.get();
	}
	
	public void setUser(User user) {
		users.set(user);
	}
	
	public void clear() {
		users.remove();
	}
}
