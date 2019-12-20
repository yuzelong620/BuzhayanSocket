package com.crxl.xllxj.module.session.manager;
 
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.crxl.xllxj.module.core.message.ResponseMsg;
import com.crxl.xllxj.module.core.net.CommandId;
import com.crxl.xllxj.module.core.net.MessageChannel;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter; 
import com.crxl.xllxj.module.session.bean.SessionBean;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class SessionManager extends BaseServiceAdapter{
	
	public static final SessionManager instance = new SessionManager();

	SessionManager() {
	}
	private static final AttributeKey<SessionBean> channelSession = AttributeKey.valueOf("channelSession"); 
	
	private static final ConcurrentHashMap<Long,SessionBean> sessionMap=new ConcurrentHashMap<Long, SessionBean>();//userId<>session

	public SessionBean getSession(Channel channl) {
		return channl.attr(channelSession).get();
	}
	public void setSession(Channel channel, SessionBean session ){
		SessionBean oldLocal=channel.attr(channelSession).get();
		if(oldLocal!=null){
		    if(oldLocal.getId()==session.getId()){//重复登录
		    	logger.info("同一个地址重复创建会话。id:"+session.getId()+",ip："+channel.remoteAddress());
		    	return;
		    }
		    sessionMap.remove(oldLocal.getId(),oldLocal);//删除上一个登录的用户信息
		}
		SessionBean old=sessionMap.putIfAbsent(session.getId(),session);
		if(old!=null){
			try{
				ResponseMsg msg=ResponseMsg.createMessage(CommandId.other_login);
				MessageChannel.sendMessage(old.getChannel(), msg);
				old.getChannel().close();//顶掉另一端登录
				logger.info("顶掉另一端登录， id:"+session.getId()+",ip："+channel.remoteAddress());
			}
			catch(Exception e){
				logger.error("关闭channel 异常：",e);
			}
		} 
		channel.attr(channelSession).set(session); 
		logger.info("创建会话， id:"+session.getId()+",ip："+channel.remoteAddress());
	}
 
	
	public void onCloseChannel(Channel channel){
		SessionBean sessionBean=channel.attr(channelSession).get();
		if(sessionBean==null){
			return;
		}
		channel.attr(channelSession).remove();//删除管道缓存
		//删除key
		sessionMap.remove(sessionBean.getId(),sessionBean);
		logger.info("关闭连接："+channel.remoteAddress());
		// 结算登录时长
		onlineService.settleOnlineTime(sessionBean.getId());
	} 
	public SessionBean findByUserId(long userId){
		return sessionMap.get(userId);
	}
	/**
	 * 当服务器关闭时调用
	 */
	public static void  onServerClose() {
		for(Long key:sessionMap.keySet()){
			SessionBean session=sessionMap.get(key);
			if(session!=null) {
				onOffLine(session);
			}
		}
	}
    /**用户下线 */
	private static void onOffLine(SessionBean session){
		sessionMap.remove(session.getId());
		session.getChannel().attr(channelSession).remove();
		logger.info("用户下线。ip:"+session.getIp()+",用户id:"+session.getId());
	}
	
	/**获取所有的在线会话  */
	public static Collection<SessionBean> getOnlineSessions(){
		return sessionMap.values();
	}
	
    /**获取在线人数  */
	public static int getOnlineNumber() {
		return sessionMap.size();
	}
}
