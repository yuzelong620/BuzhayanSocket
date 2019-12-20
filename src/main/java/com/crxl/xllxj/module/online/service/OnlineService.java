package com.crxl.xllxj.module.online.service;

import com.crxl.xllxj.module.core.config.ServerConfigLoad;
import com.crxl.xllxj.module.core.constent.GameCost;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter;
import com.crxl.xllxj.module.util.HttpClientUtils;

public class OnlineService extends BaseServiceAdapter{
	
	public void settleOnlineTime(long playerId){
    	 //TODO 异步调用http 结算
		try{
			String url=ServerConfigLoad.getServerConfig().getGamePath()+GameCost.GET_ONLINE_TIME+"/"+playerId;
			HttpClientUtils.sendGet(url);
		}
		catch(Exception e){
			logger.error("",e);
		}
	} 
    
}
