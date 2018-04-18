package com.nightchat.net;

import com.nightchat.common.Packet;

import io.netty.channel.Channel;

public class Request {
	public Packet packet;
	public Channel channel;

	public void sendMessage() {
		channel.writeAndFlush(packet);
	}
}
