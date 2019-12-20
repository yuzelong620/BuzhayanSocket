package com.crxl.xllxj.module.jetty.server;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import com.crxl.xllxj.module.jetty.servlet.DiscussReplySendServlet;
import com.crxl.xllxj.module.jetty.servlet.DynamicReplySendServlet;
import com.crxl.xllxj.module.jetty.servlet.MessageSendServlet;

public class JettyServer {
	/**
	 * 发送消息
	 */
  public static final	String PATH_SEND_MESSAGE="/sendMessage";
  
  /**广场动态回复*/
  public static final	String PATH_SEND_DYNAMIC_REPLY="/sendDynamicReply";
  
  /**剧本内评论回复*/
  public static final	String PATH_SEND_DISCUSS_REPLY="/sendDiscussReply";
  /**
   * 发送json数据的key
   */
  public static final String PARAM_JSON_KEY="json";
	
	static Logger logger=org.slf4j.LoggerFactory.getLogger(JettyServer.class);
	
	private static JettyServer instance = new JettyServer();

	public static JettyServer getInstance() {
		return instance;
	}

	JettyServer() {
	}

	public void start(int port){
		logger.info("jetty端口："+port);
		Server server=new Server(port);
		ServletContextHandler context=new ServletContextHandler(server,"/");
		server.setHandler(context); 
		context.addServlet(MessageSendServlet.class, PATH_SEND_MESSAGE);
		context.addServlet(DynamicReplySendServlet.class, PATH_SEND_DYNAMIC_REPLY);
		context.addServlet(DiscussReplySendServlet.class,  PATH_SEND_DISCUSS_REPLY);
		try{
			server.start();
//			server.join();//這裏阻塞
		}
		catch(Exception e){
			logger.error("jetty 启动异常",e);
			throw new RuntimeException(e);
		}
	}
}
