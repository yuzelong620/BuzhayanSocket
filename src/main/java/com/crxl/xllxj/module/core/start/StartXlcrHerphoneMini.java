package com.crxl.xllxj.module.core.start;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xlcr.blacklist.manager.BlackListIpManager;
import com.crxl.xllxj.module.core.config.ServerConfigLoad;
import com.crxl.xllxj.module.core.net.NettyServer;
import com.crxl.xllxj.module.core.thread.ShutdownHookThread;
import com.crxl.xllxj.module.core.thread.TaskManager;
import com.crxl.xllxj.module.jetty.server.JettyServer;

//import com.crxl.xllxj.module.identity.service.IdentityService;

//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.xlcr.mongo.config.MongoTemplateFactory;
//import org.xlcr.mongo.config.MongodbConfigLoad;
//import org.xlcr.mongo.dao.AdapterDao;

public class StartXlcrHerphoneMini {

 
	public static void main(String[] args) {
		Logger logger = null;
		try {
			File file = new File("config");
//			InputStream mongodbConfigClose = null;
			InputStream serverConfigClose = null;
			if (file.exists()) {// 外部文件
//				mongodbConfigClose = new FileInputStream(file.getPath() + "/mongo.properties");
				PropertyConfigurator.configure(file.getPath() + "/log4j.properties");
				serverConfigClose = new FileInputStream(file.getPath() + "/server.properties");
				logger = Logger.getLogger(StartXlcrHerphoneMini.class);
				logger.info("加载外部config路径配置文件:" + file.getAbsolutePath());
			} 
			else {// 类路径文件
				URL base = StartXlcrHerphoneMini.class.getClassLoader().getResource(file.getPath() + "/log4j.properties");
				PropertyConfigurator.configure(base);
//				mongodbConfigClose = ClassLoader.getSystemResourceAsStream(file.getPath() + "/mongo.properties"); // mongodb
				serverConfigClose = ClassLoader.getSystemResourceAsStream(file.getPath() + "/server.properties"); // server配置文件
				logger = Logger.getLogger(StartXlcrHerphoneMini.class);
				logger.info("加载class路径配置文件");
			}

			// 加载mongodb 配置文件
//			Properties prop = new Properties();
//			prop.load(mongodbConfigClose);
//			mongodbConfigClose.close();
//			MongoTemplate template= MongoTemplateFactory.intiDataSource(MongodbConfigLoad.load(prop));
//            AdapterDao.setMongoTemplate(template);
			// 加载server.property 配置文件
			Properties severProp = new Properties();
			severProp.load(serverConfigClose);
			ServerConfigLoad.load(severProp);
			serverConfigClose.close();

			logger.info("log4j 加载完毕，开始启动服务器");
			TaskManager.instance.init();
			logger.info("线程组初始化完毕");
//			JsonCacheManager.getInstance().init();
//			logger.info("json文件加载初始化完毕");
//			IdentityService.checkIdentityEntity();
//			logger.info("mongdb 序列号生成器检查完毕");
			new NettyServer(ServerConfigLoad.getServerConfig().getServer_port()).start();
			logger.info("netty 服务器启动完毕");
			JettyServer.getInstance().start(ServerConfigLoad.getServerConfig().getJetty_port());
			logger.info("jetty 启动完毕");
			Runtime.getRuntime().addShutdownHook(ShutdownHookThread.instance);
			
			BlackListIpManager.instance.init();
			logger.info("白名单管理器启动");
			logger.info("服务器启动完毕");

		} catch (Exception e) {
			logger.error("发生异常导致启动失败：",e);
			System.exit(0);
		}
	}
}
