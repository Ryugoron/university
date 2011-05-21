import vsFramework.Channel;
import vsFramework.Message;

/**
 * Proxy for unreliable channels that ensures message exchange (e.g. recieving)
 * according to alternating bit protocol.
 * 
 * @author Max, Alex
 */
public class AlternatingBitReceiverProxy implements Channel {

	Channel c;
	boolean alternatingBitFlag;

	public AlternatingBitReceiverProxy(Channel c) {
		this.c = c;
		this.alternatingBitFlag = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vsFramework.Channel#send(vsFramework.Message)
	 */
	@Override
	public void send(Message m) {
		c.send(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vsFramework.Channel#recv()
	 */
	@Override
	public Message recv() {
		do {
			Message m = c.recv();
			if (m == null)
				continue;

			assert (m instanceof AlternatingBitMessage);
			AlternatingBitMessage abm = (AlternatingBitMessage) m;

			if (abm.getSignalBit() != this.alternatingBitFlag) {
				// right bit, send ack and return message
				this.alternatingBitFlag = abm.getSignalBit();
				c.send(new AlternatingBitMessage(alternatingBitFlag));
				return m;
			} else {
				// wrong bit, drop message, resend ack
				c.send(new AlternatingBitMessage(alternatingBitFlag));
				continue;
			}
		} while (!c.hasBeenClosed());
		assert (c.hasBeenClosed());
		// This should never happen with connection open
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vsFramework.Channel#close()
	 */
	@Override
	public void close() {
		c.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vsFramework.Channel#hasBeenClosed()
	 */
	@Override
	public boolean hasBeenClosed() {
		return c.hasBeenClosed();
	}
}
