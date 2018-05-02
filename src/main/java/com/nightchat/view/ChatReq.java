package com.nightchat.view;

public class ChatReq {
	public int msgType;
	public String content;
	public String receiverId;

	public enum MsgType {
		TEXT(0), VOICE(1), IMG(2);
		public int value;

		private MsgType(int value) {
			this.value = value;
		}
	}
}
