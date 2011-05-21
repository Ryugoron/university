import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import vsFramework.Channel;
import vsFramework.Message;

/**
 * Proxy for unreliable channels that ensures message exchange (e.g. sending)
 * according to alternating bit protocol.
 * 
 * @author Max, Alex
 */
public class AlternatingBitSenderProxy implements Channel {

	Channel c;
	boolean alternatingBitFlag;
	final long TIMEOUT = 1000; // Timeout before resending a message
	BlockingQueue<Message> messageQueue;
	AlternatingBitMessage currentMessage;

	/**
	 * This flag mirrors the wish to close the channel. If it's false no
	 * messages are to be inserted into sendingqueue anymore.
	 */
	boolean waitingForNewMessages;

	public AlternatingBitSenderProxy(Channel c) {
		this.c = c;
		this.messageQueue = new LinkedBlockingQueue<Message>();
		this.alternatingBitFlag = true;
		this.waitingForNewMessages = true;
		this.receiverThread.start();
		this.senderThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vsFramework.Channel#send(vsFramework.Message)
	 */
	@Override
	public void send(Message m) {
		this.messageQueue.add(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vsFramework.Channel#recv()
	 */
	@Override
	public Message recv() {
		// TODO Auto-generated method stub
		return c.recv();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vsFramework.Channel#close()
	 */
	@Override
	public void close() {
		this.waitingForNewMessages = false;
		// Interrupt senderThread so that it does not get stuck on waiting for a
		// new message
		this.senderThread.interrupt();
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

	Thread senderThread = new Thread() {

		@Override
		public void run() {
			while (waitingForNewMessages || !messageQueue.isEmpty()
					|| currentMessage != null) {

				try {
					if (currentMessage == null)
						currentMessage = prepareNextMessage();
					// synchronize to avoid volatile nullvalues for
					// currentMessage that can be caused by recieverThread.
					synchronized (currentMessage) {
						c.send(currentMessage);
					}
					Thread.sleep(TIMEOUT);
				} catch (InterruptedException e) {
					continue;
				}
			}
			c.close();
		}

		/**
		 * Setup new message to be sent. This message contains the new Bitflag
		 * according to alternating bit protocol.
		 * 
		 * @return {@link AlternatingBitMessage} with next payload
		 * @throws InterruptedException
		 *             If waiting on new Message was interrupted
		 */
		private AlternatingBitMessage prepareNextMessage()
				throws InterruptedException {
			return new AlternatingBitMessage(alternatingBitFlag, messageQueue
					.take());
		}
	};

	Thread receiverThread = new Thread() {
		@Override
		public void run() {
			while (waitingForNewMessages || !messageQueue.isEmpty()
					|| currentMessage != null) {
				Message m = c.recv();
				if (m == null)
					continue;
				assert (m instanceof AlternatingBitMessage);
				AlternatingBitMessage abm = (AlternatingBitMessage) m;

				if (abm.isACK() && abm.getSignalBit() == alternatingBitFlag) {
					alternatingBitFlag = !alternatingBitFlag;
					currentMessage = null;
				}
			}
		}
	};

}
