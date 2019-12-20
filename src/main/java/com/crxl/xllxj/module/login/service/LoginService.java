package com.crxl.xllxj.module.login.service;

import com.crxl.xllxj.module.core.config.ServerConfigLoad;
import com.crxl.xllxj.module.core.constent.GameCost;
import com.crxl.xllxj.module.core.message.RequestMsg;
import com.crxl.xllxj.module.core.net.CommandId;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter;
import com.crxl.xllxj.module.login.bean.TokenloginReq;
import com.crxl.xllxj.module.session.bean.SessionBean;
import com.crxl.xllxj.module.session.manager.SessionManager;
import com.crxl.xllxj.module.util.HttpClientUtils; 
public class LoginService extends BaseServiceAdapter{
	
	public  void   loginByToken(RequestMsg request){
		TokenloginReq req=request.getBody(TokenloginReq.class);
		if(req==null||req.getToken()==null){
			request.sendErrorMessage("参数错误");
		}
		long playerId=getPlayerIdByToken(req.getToken());
		if(playerId==-1){//提示登录失败
			logger.error("登录失败token:"+req.getToken());
			request.sendCurrentCommand(CommandId.login_token_login_fail);
		 	return;
		}
		String ip=sessionService.getIp(request.getChannel());
		SessionBean session=new SessionBean(System.currentTimeMillis(), playerId, request.getChannel(),ip);
		SessionManager.instance.setSession(request.getChannel(), session);
		request.sendCurrentCommand();
	}

	private long getPlayerIdByToken(String token) {
		try{
			 String url=ServerConfigLoad.getServerConfig().getGamePath()+GameCost.GET_PLAYER_ID+"/"+token;
			 String text=HttpClientUtils.sendGet(url);
		     return Long.parseLong(text);
		}
		catch(Exception e){
			logger.error("",e);
			return -1;
		}
	}
	  
}
