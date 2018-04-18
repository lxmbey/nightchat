package com.nightchat.test;

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
						System.out.println(packet.name);
					}

					@Override
					public void channelActive(ChannelHandlerContext ctx) throws Exception {
						Packet packet = new Packet();
						packet.name = "user/login";
						packet.data = "{'aa':100}";
						ctx.writeAndFlush(packet);
					}

				});
			}
		}).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.connect("localhost", 8888);
	}
}
