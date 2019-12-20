package com.crxl.xllxj.module.core.net;

import com.alibaba.fastjson.JSONObject;
import com.crxl.xllxj.module.core.message.ResponseMsg;
import com.crxl.xllxj.module.core.thread.TaskManager;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class MessageChannel {
	
	Channel channel;

	public MessageChannel(Channel channel) { 
		this.channel = channel;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void sendMessage(ResponseMsg msg) {
		if (msg == null) {
			return;
		}
		String json = JSONObject.toJSONString(msg);
		sendStr(this.channel,json);
	}
	
	public static void sendMessage(Channel channel,ResponseMsg msg) {
		String json = JSONObject.toJSONString(msg);
		sendStr(channel, json);
	}
	
	private static void sendStr(Channel channel,String json) {
		channel.writeAndFlush(new TextWebSocketFrame(json));
		// 记录消息收发日志
		TaskManager.instance.putMessageLogger("发出消息：" + json);
	}

}
