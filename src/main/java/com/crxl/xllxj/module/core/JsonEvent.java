package com.crxl.xllxj.module.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.crxl.xllxj.module.core.message.RequestMsg;
import com.crxl.xllxj.module.core.message.ResponseMsg;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter;
/**
 * json处理任务
 * @author zxd
 *
 */
public class JsonEvent extends BaseServiceAdapter implements Runnable {
	final static long slow_time=200;//任务执行慢的时间标准
	public JsonEvent(Runnable run, RequestMsg req) {
		this.run = run;
		this.req = req;
	}
	Logger logger=LoggerFactory.getLogger(JsonEvent.class);
	Runnable run;
	RequestMsg req;

	@Override
	public void run() {
		long start=System.currentTimeMillis();
		try {
		    run.run();
		}
		catch(Exception e) { 
			onError(e);
		}
		long sub=System.currentTimeMillis()-start;
		if(sub>slow_time) {
			 logger.error("任务执行时间过慢,毫秒："+sub+"  . command:"+req.getCommandId());//记录慢的任务
		}
	} 
    //报错是记录黑名单
	private void onError(Exception ex) {
		logger.error("發生錯誤,request:"+JSONObject.toJSONString(req) ,ex);//打印报错信息
		try {
			//记录发生错误的次数
			String ip=sessionService.getIp(req.getChannel());
//			BlackListIpManager.instance.addExceptionCount(ip);
			//给客户端返回服务器错误的信息
//			sendEvent(req.getChannel(),ResponseMsg.createErrorMessage("服务器异常"));
			req.sendMessage(ResponseMsg.createErrorMessage("服务器异常"));
		}
		catch(Exception e) {
			logger.error("",e);
		}
	}

	
}
