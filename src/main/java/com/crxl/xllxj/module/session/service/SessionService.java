package com.crxl.xllxj.module.session.service;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject; 
import com.crxl.xllxj.module.core.message.ResponseMsg;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter;
import com.crxl.xllxj.module.core.thread.TaskManager;
import com.crxl.xllxj.module.session.bean.SessionBean;
import com.crxl.xllxj.module.session.manager.SessionManager;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
@Service
public class SessionService extends BaseServiceAdapter{
 
	public void setSession(Channel channl, SessionBean session ){
		 SessionManager.instance.setSession(channl, session);
	}

	public SessionBean getSession(Channel channl) {
		return SessionManager.instance.getSession(channl);
	}
	
	public void onCloseChannel(Channel channel){
		SessionManager.instance.onCloseChannel(channel);
	}
	
	public void sendMessageToOnline(ResponseMsg msg){
		if(msg==null){
			logger.error("发送的信息为空！");
			return;
		}
		if(msg.getCommandId()==0){
			throw new RuntimeException("消息缺乏 cammand");
		}
		String jsonStr=JSONObject.toJSONString(msg);
		for( SessionBean session:SessionManager.getOnlineSessions()){ 
			try {
				session.getChannel().writeAndFlush(new TextWebSocketFrame(jsonStr)); 
			}
			catch (Exception e) {
				logger.error("发送广播信息错误。",e);
			}
		}
		TaskManager.instance.putMessageLogger("在线广播信息，发出消息："+jsonStr);
	}
	
	public void sendMessage(ResponseMsg msg,long userId){
		if(msg==null){
			logger.error("发送的信息为空！");
			return;
		}
		if(msg.getCommandId()==0){ 
			logger.error("消息缺乏 cammand！");
			return;
		}
		SessionBean session= findByUserId(userId);
        if(session==null){
        	logger.error("session==nulll！用户不在线");
        	return;
        }
		String jsonStr=JSONObject.toJSONString(msg);
		try {
			session.getChannel().writeAndFlush(new TextWebSocketFrame(jsonStr)); 
			logger.info("通知消息："+jsonStr);
		}
		catch (Exception e) {
			logger.error("发送广播信息错误。",e);
		}		  
	}

	public SessionBean createByPlayer(Channel channel ) {
		long id=100L;
		String ip=channel.remoteAddress().toString();
		long lastSettleLoginTime=System.currentTimeMillis();
		SessionBean	sessionBean = new SessionBean(lastSettleLoginTime, id, channel, ip );
		SessionManager.instance.setSession(channel, sessionBean);// 存入管道
		logger.info("创建回话.ip:"+ip+",userId:"+id);
		return sessionBean;
	}
	public String getIp(Channel channel){
		String ip="";
		try{
			String str=channel.remoteAddress().toString();
			int start=str.lastIndexOf("/")+1;
			int ent=str.lastIndexOf(":");
			str=str.substring(start, ent);
			return str;
		}
		catch(Exception e){
			logger.error("",e);
		}
		return ip;
	}
	
	public SessionBean findByUserId(long userId){
		return SessionManager.instance.findByUserId(userId);
	}
	
}
