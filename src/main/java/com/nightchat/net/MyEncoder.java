package com.nightchat.net;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.nightchat.common.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class MyEncoder extends MessageToMessageEncoder<Packet> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, List<Object> out) throws Exception {
		if (msg == null) {
			return;
		}
		byte[] bytes = JSON.toJSONString(msg).getBytes("UTF-8");
		ByteBuf buf = Unpooled.buffer(bytes.length);
		buf.writeBytes(bytes);

		out.add(buf);
	}

}
