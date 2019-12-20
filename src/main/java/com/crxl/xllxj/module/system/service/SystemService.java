package com.crxl.xllxj.module.system.service;

import com.crxl.xllxj.module.core.message.RequestMsg;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter;

public class SystemService extends BaseServiceAdapter {

	public void confimOk(RequestMsg req) {
		RequestMsg obj = new RequestMsg();
		obj.setCommandId(req.getCommandId());
		obj.setBody(req.getBody());
		obj.setChannel(req.getChannel());
		jSONMessageHandlerImpl.handler(obj);
	}
}
