package com.nightchat.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class SocketClient {
	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket("120.76.103.100", 28888);
			socket.setSoTimeout(10000);
			OutputStream os = socket.getOutputStream();
			new Thread() {
				@Override
				public void run() {
					// 5秒发一个心跳
					while (true) {
						try {
							byte[] bs = "{'name':'chat/heart','data':''}".getBytes("UTF-8");
							ByteBuffer header = ByteBuffer.allocate(4);
							header.putInt(bs.length);
							os.write(header.array());
							os.write(bs);
							os.flush();
							TimeUnit.SECONDS.sleep(5);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();

			byte[] head = new byte[4];
			int length = 0;
			InputStream is = socket.getInputStream();
			while (true) {
				if (is.available() >= 4 && length == 0) {
					is.read(head);
					length = byteArrayToInt(head);
				}
				if (length != 0 && is.available() >= length) {
					byte[] data = new byte[length];
					is.read(data);
					String msg = new String(data);
					onMsg(msg);
					length = 0;
				}
				TimeUnit.MILLISECONDS.sleep(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
	}

	public static void onMsg(String msg) {
		System.out.println("收到消息" + msg);
	}
}
