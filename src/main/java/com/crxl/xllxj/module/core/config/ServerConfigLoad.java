package com.crxl.xllxj.module.core.config;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject; 

public class ServerConfigLoad {

	private static ServerConfig serverConfig;

	public static ServerConfig getServerConfig() {
		return serverConfig;
	}

	private ServerConfigLoad() {
	}

	static Logger logger = LoggerFactory.getLogger(ServerConfigLoad.class);

	public static void load(Properties prop) {
		logger.info("加载 server.property 配置文件：" + JSONObject.toJSONString(prop));

		int server_port = readInt(prop, "server_port", 9089);// 程序端口
		boolean ssl_open = readBoolean(prop, "ssl_open", false);
		String ssl_filename = prop.getProperty("ssl_filename", "/config/1102598__edisonluorui.com.jks");
		String ssl_password = prop.getProperty("ssl_password");
        String ssl_type     = prop.getProperty("ssl_type");
        String main_server_address=prop.getProperty("main_server_address");
        int jetty_port=readInt(prop, "jetty_port", 80);
        int thread_max_task_size=readInt(prop, "thread_max_task_size", 500);// 线程最大任务数量
        String gamePath=prop.getProperty("gamePath");
		ServerConfig server = new ServerConfig();
			server.setServer_port(server_port);
			server.setSsl_open(ssl_open);
			server.setSsl_filename(ssl_filename);
			server.setSsl_password(ssl_password);
			server.setSsl_type(ssl_type);
			server.setMain_server_address(main_server_address);
			server.setJetty_port(jetty_port);
			server.setThread_max_task_size(thread_max_task_size);
			server.setGamePath(gamePath);
		ServerConfigLoad.serverConfig=server;

	}

	public static boolean readBoolean(Properties prop, String key, boolean defaultValue) {
		if (prop.containsKey(key)) {
			return Boolean.parseBoolean(prop.getProperty(key));
		}
		return defaultValue;
	}

	public static int readInt(Properties prop, String key, int defaultValue) {
		if (prop.containsKey(key)) {
			return Integer.parseInt(prop.getProperty(key));
		}
		return defaultValue;
	}

}
