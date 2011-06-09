package game.networking;

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
}
