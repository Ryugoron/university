import vsFramework.Message;

/**
 * Message used by alternating bit protocol. Unlike conventional messages
 * AlternatingBitMessage wraps the payload (i.e. the {@link Message}) with an
 * additional bit flag according to alternating bit protocol.
 * 
 * @author Max, Alex
 * 
 */
public class AlternatingBitMessage implements Message {
	final Message wrappedMessage;
	final boolean signalBit;

	public AlternatingBitMessage(boolean signalBit, Message m) {
		this.signalBit = signalBit;
		this.wrappedMessage = m;
	}

	/**
	 * Creates an {@link AlternatingBitMessage} without any payload. Used to
	 * represent <code>ACK</code>s send by message receiver.
	 * 
	 * @param signalBit
	 */
	public AlternatingBitMessage(boolean signalBit) {
		this.signalBit = signalBit;
		this.wrappedMessage = null;
	}

	public boolean isACK() {
		return this.wrappedMessage == null;
	}

	public boolean getSignalBit() {
		return this.signalBit;
	}

	@Override
	public byte[] getData() {
		assert (this.wrappedMessage != null);
		return this.wrappedMessage.getData();
	}

	@Override
	public int getLength() {
		assert (this.wrappedMessage != null);
		return this.wrappedMessage.getLength();
	}

	@Override
	public String toString() {
		if (this.wrappedMessage == null)
			return "ACK " + Boolean.toString(this.signalBit);
		else
			return this.wrappedMessage.toString();
	}
}
