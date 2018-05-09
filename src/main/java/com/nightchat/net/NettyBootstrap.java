package com.nightchat.net;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nightchat.common.Const;
import com.nightchat.common.Functions;
import com.nightchat.common.Functions.MethodWrapper;
import com.nightchat.common.NotLogin;
import com.nightchat.common.Packet;
import com.nightchat.view.UserInfoData;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class NettyBootstrap {
	private Logger log = LogManager.getLogger(getClass());

	@Value("${netty.port}")
	private int port;

	@PostConstruct
	public void start() {
		EventLoopGroup boss = new NioEventLoopGroup(1);
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(boss, boss).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast(new LengthFieldPrepender(4));
				pipeline.addLast(new IdleStateHandler(11, 0, 0, TimeUnit.SECONDS));
				pipeline.addLast(new MyEncoder());
				pipeline.addLast(new MyDecoder());

				pipeline.addLast(new ChannelInboundHandlerAdapter() {

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						Request request = new Request();
						request.packet = (Packet) msg;
						request.channel = ctx.channel();
						MethodWrapper method = Functions.getMethod(request.packet.name);
						if (method == null) {
							log.warn("不存在的协议" + request.packet.name);
							return;
						}
						NotLogin notLogin = method.method.getAnnotation(NotLogin.class);
						if (notLogin == null && !Const.onlineChannel.containsKey(ctx.channel())) {
							log.warn("未登录操作");
							ctx.channel().close();
							return;
						}
						Object message = method.method.invoke(method.instance, request);
						if (message != null) {
							Packet packet = new Packet();
							packet.name = request.packet.name;
							packet.data = message.toString();
							ctx.channel().writeAndFlush(packet);
						}
					}

					@Override
					public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
						log.error("", cause);
					}

					@Override
					public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
						if (evt instanceof IdleStateEvent) {
							log.warn("空闲超时");
							ctx.channel().close();
						}
					}

					@Override
					public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
						UserInfoData userInfo = Const.onlineChannel.remove(ctx.channel());
						if (userInfo != null) {
							Const.onlineUser.remove(userInfo.id);
						}
					}

				});
			}
		}).option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		bootstrap.bind(port);
	}
}
