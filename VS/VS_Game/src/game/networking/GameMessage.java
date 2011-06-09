package game.networking;

import vsFramework.AbstractMessage;
import vsFramework.Message;

public class GameMessage extends AbstractMessage implements Message {

	public GameMessage(GameMessageType message) {
		this.data = message.toString().getBytes();
	}

	public static GameMessageType fromMessage(Message message) throws IllegalArgumentException {
		return GameMessageType.valueOf(new String(message.getData()));
	}

	public enum GameMessageType {
		HELLO("HELLO"), OLLEH("OLLEH"), PEERS("PEERS"), SREEP("SREEP");

		String message;

		GameMessageType(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return this.message;
		}
	}
}
