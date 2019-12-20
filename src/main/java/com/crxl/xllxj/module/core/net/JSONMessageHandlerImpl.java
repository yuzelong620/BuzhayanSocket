package com.crxl.xllxj.module.core.net;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.crxl.xllxj.module.core.JsonEvent;
import com.crxl.xllxj.module.core.message.RequestMsg;
import com.crxl.xllxj.module.core.message.ResponseMsg;
import com.crxl.xllxj.module.core.service.BaseServiceAdapter;
import com.crxl.xllxj.module.core.thread.TaskManager;
import com.crxl.xllxj.module.session.bean.SessionBean;

@Service
public class JSONMessageHandlerImpl extends BaseServiceAdapter implements JSONMessageHandler {

	private static Map<Integer, JsonEvent> commands = new HashMap<>();

	public void register(JsonEvent event, int cmdId) {
		if (commands.containsKey(cmdId)) {
			throw new RuntimeException("命令号重复");
		}
		commands.put(cmdId, event);
	}

	// other cmd
	private void loginHandler(RequestMsg req) {
		switch (req.getCommandId()) {
		case CommandId.login_token_auto_login:
			loginService.loginByToken(req);
			break;
		default:
			ResponseMsg msg = ResponseMsg.createErrorMessage("无效指令。");
			req.sendMessage(msg);
			break;
		}
	}

	// game cmd
	private void gameHandler(RequestMsg req) {
		SessionBean session = sessionService.getSession(req.getChannel());
		if (session == null) {
			req.sendMessage(ResponseMsg.createMessage(CommandId.login_visitors_relogin));
			return;
		}
		switch (req.getCommandId()) {
		default:
			ResponseMsg msg = ResponseMsg.createErrorMessage("无效指令");
			req.sendMessage(msg);
			break;
		}
	}

	@Override
	public void handler(RequestMsg req) {
		int commandType = req.getCommandId() / 100000;
		switch (commandType) {
		case CommandId.command_type_system:// 0 system
			Runnable systemTask = new Runnable() {
				public void run() {
					systemHandler(req);
				}
			};
			TaskManager.instance.putLoginTask(new JsonEvent(systemTask, req));
			break;
		case CommandId.command_type_common:// 1公共命令
			Runnable loginTask = new Runnable() {
				public void run() {
					loginHandler(req);
				}
			};
			TaskManager.instance.putLoginTask(new JsonEvent(loginTask, req));
			break;
		case CommandId.command_type_game:// 2游戏命令
			Runnable gameTask = new Runnable() {
				public void run() {
					gameHandler(req);
				}
			};
			TaskManager.instance.putMainTask(req.getChannel(), new JsonEvent(gameTask, req));
			break;
		default:
			ResponseMsg msg = ResponseMsg.createErrorMessage("无效指令。");
			req.sendMessage(msg);
			break;
		}
	}

	private void systemHandler(RequestMsg req) {
		switch (req.getCommandId()) {
		case CommandId.system_confim:
			systemService.confimOk(req);
			break;
		default:
			ResponseMsg msg = ResponseMsg.createErrorMessage("无效指令。");
			req.sendMessage(msg);
			break;
		}
	}
}
