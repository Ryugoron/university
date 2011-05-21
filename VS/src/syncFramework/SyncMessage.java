package syncFramework;

import vsFramework.Message;

public class SyncMessage implements Message{
	
	//Kleiner Abschnitt mit standard Mitteilungen
	public static final byte[] PROCESS_SENDED = "send".getBytes();
	public static final byte[] SEND_ACK = "sendAck".getBytes();
	public static final byte[] PROCESS_WORKED = "work".getBytes();
	public static final byte[] WORK_ACK = "workAck".getBytes();
	public static final byte[] PROCESS_ENDED = "finish".getBytes();
	
	private byte[] data;
	private int length;
	
	public SyncMessage(byte[] payload) {
		this.data = payload;
		this.length = payload.length;
	}
	
	public SyncMessage(byte[] payload, int length) {
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
