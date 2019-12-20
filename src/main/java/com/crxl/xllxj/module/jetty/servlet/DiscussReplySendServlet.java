package com.crxl.xllxj.module.jetty.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.crxl.xllxj.module.core.message.ResponseMsg;
import com.crxl.xllxj.module.core.net.CommandId;
import com.crxl.xllxj.module.session.service.SessionService;

public class DiscussReplySendServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(DiscussReplySendServlet.class);
	 SessionService sessionService=new SessionService();
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, java.io.IOException {
		String json=req.getParameter("json");
		if(json==null||"".equals(json)){
			return;
		}
		JSONObject obj=JSONObject.parseObject(json);
		Long userId=obj.getLong("toPlayerId");
		if(userId==null){
			return;
		}
		ResponseMsg msg=ResponseMsg.createMessage(json, CommandId.game_discuss_send);
		sessionService.sendMessage(msg, userId);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}
}
