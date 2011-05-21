import vsFramework.*;

public class HelloWorld {

	// configuration of our HelloWorld

	// the sender side will add some message loss and delay
	final static double lossrate = 0.1; // 0 means all messages are delivered, 1
	// none
	final static double delay_min = 0.0; // min delay in seconds
	final static double delay_max = 1.5; // max delay in seconds

	// a we will send our message through an ordinary pipe
	static BidirectionalPipe p = new BidirectionalPipe();
	// and add some fun for the endpoints
	static Channel senderChannel = new AlternatingBitSenderProxy(
			new RandomDelayChannelProxy(new LossyChannelProxy(p.gehtLeft(),
					lossrate), delay_min, delay_max));
	static Channel receiverChannel = new AlternatingBitReceiverProxy(
			new LossyChannelProxy(p.gehtRight(), 0.1));

	public static void main(String[] args) throws InterruptedException {

		System.err.println("Starting receiver");
		receiver.start();

		System.err.println("Sending message");
		for (Message m : ByteMessage.fromString("Hello World!")) {
			senderChannel.send(m);
		}

		System.err.println("Closing channel");
		senderChannel.close();

		receiver.join();
		System.err.println("\nReceiver finished");

	}

	static Thread receiver = new Thread() {
		public void run() {
			while (true) {
				Message m = receiverChannel.recv();
				if (m != null) {
					System.out.print(m.toString());
				} else if (receiverChannel.hasBeenClosed()) {
					return;
				} else {
					System.err.println("receiver error!");
				}
			}

		}
	};

}
