package com.crxl.xllxj.module.core.thread;
 

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.xlcr.thread.WorkThread;

import io.netty.channel.Channel;

public class WorkChannelThread extends WorkThread{
	
    public WorkChannelThread(String name) {
		super(name); 
	}
    ConcurrentHashSet<Channel> channels=new ConcurrentHashSet<>();
    
    public void register(Channel channel) {
    	channels.add(channel);
    }
    
    public int getChannelSize() {
    	return channels.size();
    }
    public void delete(Channel channel) {
    	channels.remove(channel);
    }
}
