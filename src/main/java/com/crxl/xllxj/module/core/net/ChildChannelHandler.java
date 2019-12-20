package com.crxl.xllxj.module.core.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
 
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crxl.xllxj.module.core.config.ServerConfig;
import com.crxl.xllxj.module.core.config.ServerConfigLoad;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
@Component
public class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
	org.slf4j.Logger logger=LoggerFactory.getLogger(ChildChannelHandler.class);
	@Autowired
	private MainWebSocket mainWebSocket =new MainWebSocket() ;
	SslHandler sslHandler;
	@Override
	protected void initChannel(SocketChannel e) throws Exception {
		ServerConfig serverConfig=ServerConfigLoad.getServerConfig();
        if(serverConfig.isSsl_open()){//ssl证书开关打开
    		SSLContext sslContext =  createSSLContext(serverConfig.getSsl_type(),serverConfig.getSsl_password(),serverConfig.getSsl_filename());
    		SSLEngine sslEngine = sslContext.createSSLEngine(); 
    		sslEngine.setUseClientMode(false); /// 是否使用客户端模式 sslEngine.setNeedClientAuth(false); ////是否需要验证客户端
    		e.pipeline().addLast("ssl",new SslHandler(sslEngine));
        }
//        else{
//        	logger.info("ssl_open=false");
//        }
		e.pipeline().addLast("http-codec",new HttpServerCodec());
		e.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
		e.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
		e.pipeline().addLast("idleStateHandler",new IdleStateHandler(150000,0, 0, TimeUnit.SECONDS));
		e.pipeline().addLast("handler",mainWebSocket);
	}
	  
//	public static SSLContext createSSLContext( ) throws Exception {
//		String type="JKS";
//		String password="123456";
//	    KeyStore ks = KeyStore.getInstance(type); 
//	    InputStream ksInputStream=ChildChannelHandler.class.getClass().getResourceAsStream("/config/xldmx.edisonluorui.com.jks"); 
//	    ks.load(ksInputStream, password.toCharArray());
//	 	//KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
//	    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
//	    kmf.init(ks, password.toCharArray());  //SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
//	    SSLContext sslContext = SSLContext.getInstance("TLS");
//	    sslContext.init(kmf.getKeyManagers(), null, null);
//	    return sslContext;
//	}
	public   SSLContext createSSLContext( String type,String password,String fileName ) throws Exception {
//	String type="JKS";
//	String password="G9QCpmbY";
//    InputStream ksInputStream=ChildChannelHandler.class.getClass().getResourceAsStream("/config/1102598__edisonluorui.com.jks");
	InputStream ksInputStream = getFileInputStream(fileName);
//	logger.info("---->ssl file:----->"+ksInputStream);
	KeyStore ks = KeyStore.getInstance(type); 
    ks.load(ksInputStream, password.toCharArray());
 	//KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
    kmf.init(ks, password.toCharArray());  //SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(kmf.getKeyManagers(), null, null);
    return sslContext;
}

 

private InputStream getFileInputStream(String fileName) throws FileNotFoundException {
		InputStream ksInputStream=null;
		File file=new File("config/"+fileName);
		if(file.exists()){
			ksInputStream= new FileInputStream(file);
		}
		else{
			logger.info("读取jar内部秘钥文件："+fileName);
		    ksInputStream=ChildChannelHandler.class.getClass().getResourceAsStream("/config/"+fileName);
		}
		return ksInputStream;
	}
	
} 