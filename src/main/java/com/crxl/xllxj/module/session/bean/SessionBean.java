package com.crxl.xllxj.module.session.bean;


import io.netty.channel.Channel;

/**
 * 会话
 */
public class SessionBean {
	
	public SessionBean(long lastSettleLoginTime, long id, Channel channel ,String ip) {
	    this.lastSettleLoginTime = lastSettleLoginTime;
		this.id = id;
		this.channel = channel; 
		this.ip = ip; 
	}

	public SessionBean() {
	}

	long lastSettleLoginTime;
	long id;
	Channel channel; 
	String ip;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getLastSettleLoginTime() {
		return lastSettleLoginTime;
	}

	public void setLastSettleLoginTime(long lastSettleLoginTime) {
		this.lastSettleLoginTime = lastSettleLoginTime;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}


}
