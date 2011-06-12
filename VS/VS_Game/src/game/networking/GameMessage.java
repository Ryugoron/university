package game.networking;

import java.util.List;

import vsFramework.Message;

public enum GameMessage implements Message {
	HELLO("HELLO"), OLLEH("OLLEH"), PEERS("PEERS"), SREEP("SREEP");

	final String message;

	GameMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return this.message;
	}

	@Override
	public byte[] getData() {
		return this.message.getBytes();
	}

	@Override
	public int getLength() {
		return this.message.length();
	}

	public static GameMessage fromMessage(Message message)
			throws IllegalArgumentException {
		return GameMessage.valueOf(new String(message.getData()));
	}

	public Message toMessage(String[] param)
			throws IllegalArgumentException {

//		StringBuilder sb = new StringBuilder();
//		sb.append(this.toString());
//		for (int i = 0; i < param.length; ++i) {
//			sb.append(" ");
//			sb.append(param[i]);
//		}
//		sb.trimToSize();
		String m = this.message;
		for(int i=0; i<param.length; ++i){
			m += " " + param[i];
		}
		System.out.println(m);
		return new UDPMessage(m.getBytes());
	}

	public Message toMessage(List<String> param)
			throws IllegalArgumentException {

//		StringBuilder sb = new StringBuilder();
//		sb.append(this.toString());
//		for (String p : param) {
//			sb.append(" ");
//			sb.append(p);
//		}
//		sb.trimToSize();
//		return new UDPMessage(sb.toString().getBytes());
		
		String m = this.message;
		for(String s : param){
			m += " " + s;
		}
		return new UDPMessage(m.getBytes());
	}
}
