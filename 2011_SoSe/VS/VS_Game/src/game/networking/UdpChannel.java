package game.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import vsFramework.Channel;
import vsFramework.Message;

public class UdpChannel implements Channel {
	InetSocketAddress remote;
	DatagramSocket socket;
	BlockingQueue<Message> recieved;
	boolean closed = false;

	public UdpChannel(DatagramSocket socket) {
		this.socket = socket;
		this.recieved = new LinkedBlockingQueue<Message>();
	}

	@Override
	public void send(Message m) {
		if(this.remote == null){
			System.err.println("Noch kein Endpunkt vorhanden");
			return;
		}
		DatagramPacket outgoing = new DatagramPacket(m.getData(),
				m.getLength(), this.remote.getAddress(), this.remote.getPort());
		try {
			this.socket.send(outgoing);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Message recv() {
		try {
			Message m = this.recieved.take();
			return m;
		} catch (InterruptedException e) {
			return recv();
		}
	}
	
	public Message nrecv(){
		return this.recieved.poll();
	}

	@Override
	public void close() {
		this.closed = UdpChannelFactory.closeChannel(this);
	}

	@Override
	public boolean hasBeenClosed() {
		return this.closed;
	}

	/**
	 * @return the remote address we are connected to.
	 */
	public InetAddress getRemoteAddress() {
		return (this.remote == null) ? null : this.remote.getAddress();
	}

	public int getRemotePort() {
		return (this.remote == null) ? null : this.remote.getPort();
	}

	public int getLocalPort() {
		return this.socket.getLocalPort();
	}

	void connect(InetAddress remote, int port) {
		this.remote = new InetSocketAddress(remote, port);
	}

}
