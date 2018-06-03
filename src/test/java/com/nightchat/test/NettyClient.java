package com.nightchat.test;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.nightchat.common.Packet;
import com.nightchat.net.MyDecoder;
import com.nightchat.net.MyEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NettyClient {
	public static void main(String[] args) {
		EventLoopGroup group = new NioEventLoopGroup(1);
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast(new LengthFieldPrepender(4));
				pipeline.addLast(new MyEncoder());
				pipeline.addLast(new MyDecoder());

				pipeline.addLast(new ChannelInboundHandlerAdapter() {

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						Packet packet = (Packet) msg;
						System.out.println("收到服务器消息：" + JSON.toJSONString(packet));
					}

					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						Packet packet = new Packet();
						packet.name = "chat/heart";
						packet.data = "";

						new Thread() {
							public void run() {
								while (true) {
									ctx.writeAndFlush(packet);
									try {
										TimeUnit.SECONDS.sleep(5);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}.start();
					}

				});
			}
		}).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.connect("120.76.103.100", 28888);
	}
}
