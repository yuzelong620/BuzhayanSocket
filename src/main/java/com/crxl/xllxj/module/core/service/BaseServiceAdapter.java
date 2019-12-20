package com.crxl.xllxj.module.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crxl.xllxj.module.core.net.JSONMessageHandler;
import com.crxl.xllxj.module.core.net.JSONMessageHandlerImpl;
import com.crxl.xllxj.module.login.service.LoginService;
import com.crxl.xllxj.module.online.service.OnlineService;
import com.crxl.xllxj.module.session.service.SessionService;
import com.crxl.xllxj.module.system.service.SystemService;

public class  BaseServiceAdapter {
	
	protected static Logger logger=LoggerFactory.getLogger(BaseServiceAdapter.class); 
//	protected static IdentityDao identityDao=new IdentityDao(); 
	protected static JSONMessageHandler jSONMessageHandlerImpl=new JSONMessageHandlerImpl();
	protected static LoginService loginService=new LoginService();
	protected static SessionService sessionService=new SessionService();
	protected static SystemService systemService=new SystemService();
	protected static OnlineService onlineService=new OnlineService();
}
