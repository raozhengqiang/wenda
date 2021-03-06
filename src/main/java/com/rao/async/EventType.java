package com.rao.async;

/**
 * Created by nowcoder on 2016/7/30.
 */
//事件等级

public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
	FOLLOW(4),
    UNFOLLOW(5);

    private int value;
    EventType(int value) { this.value = value; }
    public int getValue() { return value; }
}
