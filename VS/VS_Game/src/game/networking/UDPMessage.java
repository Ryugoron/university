package game.networking;

import vsFramework.Message;


public class UDPMessage implements Message{
	
	private byte[] data;
	private int length;
	
	public UDPMessage(byte[] payload) {
		this.data = payload;
		this.length = payload.length;
	}
	
	public UDPMessage(byte[] payload, int length) {
		this.data = payload;
		this.length = length;
	}
	
	@Override
	public int getLength() {
		return this.length;
	}

	@Override
	public byte[] getData() {
		return this.data;
	}

}
