package com.crxl.xllxj.module.core.message;

import com.crxl.xllxj.module.core.net.CommandId;

public class ResponseMsg {
	
	public ResponseMsg() { 
	}
	
	private ResponseMsg(Object data,int commandId) {
		this.commandId=commandId;
		this.body = data;
	}
	

	Object body;//消息体
	int commandId;
	/**
	 * 提示消息
	 * @param info
	 * @return
	 */
	public static ResponseMsg createInfoMessage(String info){
		return ResponseMsg.createMessage(info, CommandId.system_info); 
	}
	/**
	 * 错误消息 
	 */
	public static  ResponseMsg createErrorMessage(String info){
		return ResponseMsg.createMessage(info, CommandId.system_error);  
	} 
	/**
	 * 创建消息
	 * @param data
	 * @param commandId
	 * @return
	 */
	public static ResponseMsg createMessage(Object data,int commandId) {
		return new ResponseMsg(data, commandId);
	}
	/**
	 * 创建消息 . 没有消息体
	 * @param commandId
	 * @return
	 */
	public static ResponseMsg createMessage(int commandId) {
		return new ResponseMsg(null, commandId);
	}
   
	
	 
	
	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}
 

}
