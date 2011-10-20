package game.networking;

import java.util.Arrays;

import vsFramework.Message;


public class UDPMessage implements Message{
	
	private byte[] data;
	private int length;
	
	public UDPMessage(byte[] payload) {
		this.length = payload.length;
		this.data = Arrays.copyOf(payload,this.length);
	}
	
	public UDPMessage(byte[] payload, int length) {
		this.length = length;
		this.data = Arrays.copyOf(payload,this.length);
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
