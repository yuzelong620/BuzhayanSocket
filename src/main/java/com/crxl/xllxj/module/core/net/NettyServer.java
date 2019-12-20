package com.crxl.xllxj.module.core.net;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
 
@Component
public class NettyServer  implements InitializingBean {
	
	Logger log=LoggerFactory.getLogger(NettyServer.class);
	
	public static NettyServer nettyServer;//单例用于适应spring
        
		public NettyServer(int port){
			this.port=port;
			nettyServer=this;
	    }

		private Integer port;//默认9088

		@Autowired
		private ChildChannelHandler childChannelHandler =new ChildChannelHandler();

		private EventLoopGroup bossGroup;

		private EventLoopGroup workGroup;

		/**
		 * start nettyServer
		 */
		public void start() {

			bossGroup = new NioEventLoopGroup();
			workGroup = new NioEventLoopGroup();
			try {
				ServerBootstrap b = new ServerBootstrap();
				b.group(bossGroup, workGroup);
				b.channel(NioServerSocketChannel.class);
				b.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536));
				b.childHandler(childChannelHandler);
				//WebSocket 绑定端口
				log.info("netty websocket 绑定端口："+port);
				b.bind(port).sync();

			} catch (Exception e) {
				bossGroup.shutdownGracefully();
				workGroup.shutdownGracefully();
				log.error("",e);
				throw new RuntimeException(e);

			} finally {
			}
		}

		@PreDestroy
		public void stop() {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}

		@Override
		public void afterPropertiesSet() {

		}
}
