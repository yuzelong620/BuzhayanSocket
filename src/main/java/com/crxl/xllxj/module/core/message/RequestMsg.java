package com.crxl.xllxj.module.core.message;

import com.alibaba.fastjson.JSONObject;
import com.crxl.xllxj.module.core.net.MessageChannel;
import com.crxl.xllxj.module.core.thread.TaskManager;

import io.netty.channel.Channel;

public class RequestMsg {
	
	protected MessageChannel messageChannel;
	protected int commandId = 0;
	protected String body;
	/**发送錯誤消息 */
	public void sendErrorMessage(String info){
		this.messageChannel.sendMessage(ResponseMsg.createErrorMessage(info));
	}
	/**发送提示消息 */
	public void sendInfoMessage(String info){
		this.messageChannel.sendMessage(ResponseMsg.createInfoMessage(info));
	}
	/**发送消息 */
	public void sendMessage(ResponseMsg obj){
		this.messageChannel.sendMessage(obj);
	}
	/**发送 返回当前命令 */
	public void sendCurrentCommand(){
		this.messageChannel.sendMessage(ResponseMsg.createMessage(commandId));
	}
	/**
	 * 发送 返回当前命令 
	 * @param data 返回的数据（pojo）
	 */
	public void sendCurrentCommand(Object data){
		this.messageChannel.sendMessage(ResponseMsg.createMessage(data,commandId));
	}
	/**发送 特定命令*/
	public void sendCurrentCommand(int commandId){
		this.messageChannel.sendMessage(ResponseMsg.createMessage(commandId));
	}
	public void putSaveTask(Runnable task){
		TaskManager.instance.putSaveTask(getChannel(),task);
	}
	 
	public void putSaveTaskByHash(Runnable task){
		TaskManager.instance.putSaveTaskByHash(task);
	}
	
	public MessageChannel getMessageChannel() {
		return messageChannel;
	}
    
	public Channel getChannel() {
		return this.messageChannel.getChannel();
	}

	public void setChannel(Channel channel) {
		this.messageChannel=new MessageChannel(channel);
	}
	
	public <T> T getBody(Class<T> clz) {
		return JSONObject.parseObject(body, clz);
	}
	
	////getter and setter !////

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	public int getCommandId() {
		return commandId;
	}
	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}
	 

}
