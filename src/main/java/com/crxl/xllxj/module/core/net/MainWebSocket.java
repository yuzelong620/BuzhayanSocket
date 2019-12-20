package com.crxl.xllxj.module.core.net;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xlcr.blacklist.manager.BlackListIpManager;

import com.alibaba.fastjson.JSONObject;
import com.crxl.xllxj.module.core.message.RequestMsg;
import com.crxl.xllxj.module.core.message.ResponseMsg;
import com.crxl.xllxj.module.core.thread.TaskManager;
import com.crxl.xllxj.module.session.service.SessionService;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class MainWebSocket extends SimpleChannelInboundHandler<Object> {
 
	JSONMessageHandlerImpl jSONMessageHandlerImpl = new JSONMessageHandlerImpl();
	SessionService sessionService=new SessionService();
	Logger log = LoggerFactory.getLogger(MainWebSocket.class);

	private WebSocketServerHandshaker handshaker;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//判断黑名单
		String ip=sessionService.getIp(ctx.channel());
		if(ip!=null&&BlackListIpManager.instance.isBlackIp(ip)) {//黑名单ip，直接关闭
			ctx.channel().close();
			return;
		}
		TaskManager.instance.registerChannelThread(ctx.channel());
//		log.info("建立连接："+ip);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		sessionService.onCloseChannel(ctx.channel()); 
		ctx.channel().close();
//		log.info("关闭连接："+ctx.channel().remoteAddress().toString());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg instanceof FullHttpRequest) {
				ReferenceCountUtil.retain(msg);
				handleHttpRequest(ctx, ((FullHttpRequest) msg));
			}else
			if (msg instanceof WebSocketFrame) {
				handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
			}
		} catch (Exception e) {
			log.error("业务处理异常", e); 
		} catch (Throwable e) {
			log.error("系统崩溃", e); 
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
		}
		else if (frame instanceof TextWebSocketFrame) {
			String msg = ((TextWebSocketFrame) frame).retain().text();
			if (null == msg || "".equals(msg))
				return;
			if (msg.equalsIgnoreCase("h&b")) {
				ctx.channel().writeAndFlush(frame.retain());
				return;
			}
			try {
				if(closed){//服务器已经关闭--不在接收消息
					log.info("服务器进入关闭程序 ，不再处理消息");
					ctx.channel().writeAndFlush(closeServerJsonMessage);
					return;
				}
				TaskManager.instance.putMessageLogger("收到消息："+msg);
				RequestMsg req = JSONObject.parseObject(msg, RequestMsg.class);
				req.setChannel(ctx.channel());//设置管道信息
				
				jSONMessageHandlerImpl.handler(req);
			}
			catch (Throwable e) {
				log.error("错误："+msg, e);
				String ip=sessionService.getIp(ctx.channel());
				if(ip!=null&&BlackListIpManager.instance.isBlackIp(ip)) {//黑名单ip，直接关闭
					ctx.channel().close();
					return;
				}
				BlackListIpManager.instance.addExceptionCount(ip);
			}
		}
	}
	//服务器已经关闭提示
    static String closeServerJsonMessage=JSONObject.toJSONString(ResponseMsg.createInfoMessage("服务器维护中"));
    /**
     *消息入口 开关
     */
    public  static volatile boolean closed=false;
    
	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("127.0.0.1", null, false);
		handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		String ip=sessionService.getIp(ctx.channel());
		BlackListIpManager.instance.addExceptionCount(ip);//记录指定ip 连接的报错次数
		if(cause instanceof io.netty.util.IllegalReferenceCountException) {//客户端已经关闭连接
			ctx.channel().close();
		}
		else{
			log.info("exceptionCaught()发生异常 .ip："+ctx.channel().remoteAddress().toString(),cause);
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

		if (evt instanceof IdleStateEvent) {

			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				log.info("netty框架监听到心跳，读超时，强行关闭");
				sessionService.onCloseChannel(ctx.channel());
				ctx.channel().close();
			} else if (event.state() == IdleState.WRITER_IDLE) {
				/** 写超时 */
				log.error("===服务端===(WRITER_IDLE 写超时)");
			} else if (event.state() == IdleState.ALL_IDLE) {
				/** 总超时 */
				log.error("===服务端===(ALL_IDLE 总超时)");
			}
		}
	}
	
	

}