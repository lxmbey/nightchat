package com.nightchat.net;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.nightchat.common.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MyDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		byte[] bytes = new byte[in.readableBytes()];
		in.readBytes(bytes);
		Packet packet = JSON.parseObject(new String(bytes, "UTF-8"), Packet.class);
		out.add(packet);
	}

}
