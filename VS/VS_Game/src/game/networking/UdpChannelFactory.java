package game.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;


public class UdpChannelFactory {
	protected static Map<Integer, MessageDispatcher> dispatcher = new ConcurrentHashMap<Integer, MessageDispatcher>();
	
	private static synchronized UdpChannel createChannel(int local_port){
		UdpChannel channel;
		if (dispatcher.containsKey(local_port)) {
			// port already exists, add new channel to according dispatcher
			channel = new UdpChannel(dispatcher.get(local_port).socket);
			MessageDispatcher m = dispatcher.get(local_port);
			m.addUnconnectedChannel(channel);
		} else {
			// channel on new port, so create new dispatcher and socket
			DatagramSocket newSock;
			try {
				newSock = new DatagramSocket(local_port);
				channel = new UdpChannel(newSock);
				MessageDispatcher m = new MessageDispatcher(newSock);
				dispatcher.put(local_port, m);				m.start();
			} catch (SocketException e) {
				System.err.println("Socketcreation failed on port "
						+ local_port + "\n");
				return null;
			}
		}
		return channel;
	}
	
	/**
	 * Builds a new UDP channel which may be used so receive a first packet from
	 * an arbitrary endpoint and send further messages to it multiple calls to
	 * this share a socket
	 * 
	 * @param local_port
	 *            port to listen on
	 * @return
	 */
	public static UdpChannel newUdpChannel(int local_port) {
		UdpChannel channel = createChannel(local_port);
		dispatcher.get(local_port).addUnconnectedChannel(channel);
		return channel;
	}

	/**
	 * Builds a new UDP channel which may be used for sending and receiving
	 * messages to a specified endpoint
	 * 
	 * @param local_port
	 *            port to listen on
	 * @param remote_addr
	 *            address to connect to
	 * @param remote_port
	 *            port to connecht to
	 * @return
	 */
	public static UdpChannel newUdpChannel(int local_port,
			InetAddress remote_addr, int remote_port) {
		UdpChannel udpChannel = createChannel(local_port);

		MessageDispatcher m = dispatcher.get(local_port);
		m.connectEndpoint(remote_addr, remote_port, udpChannel);
		return udpChannel;
	}
	

	public static boolean closeChannel(UdpChannel channel) {
		MessageDispatcher m = dispatcher.get(channel.getLocalPort());
		if (m != null) {
			synchronized (m) {
				m.unconnectedChannels.remove(channel);
				if (channel.getRemoteAddress() != null)
					m.endpoints.remove(new InetSocketAddress(channel
							.getRemoteAddress(), channel.getRemotePort()));
				if (m.endpoints.isEmpty() && m.unconnectedChannels.isEmpty()) {
					// All channels closed? Close socket then
					m.socket.close();
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * <p>Der aufrufende Prozess muss solange warten,
	 * bis der MessageDispatcher des Portes eine Nachricht empfangen hat.
	 * </p>
	 * 
	 * <p>
	 * Aufgrund nicht gelöster Probleme in Dingen Synchronisation wird
	 * man nach einiger Zeit automatisch geweckt um selber einmal nachzusehen.
	 * <br />
	 * Dies ist nötig, da bei einem Aufruf von waitOnPort Nachrichten empfangen worden sein
	 * können, aber man noch nicht synchronized war.
	 * </p>
	 * 
	 * <p>
	 * Spezielle Form von {@link UdpChannelFactory#waitForMessage()}
	 * </p>
	 * 
	 * @param Port auf den gelauscht werden soll. (Eintreffen einer Nachricht)
	 */
	public static void waitOnPort(int Port) throws InterruptedException{
		MessageDispatcher dis = dispatcher.get(Port);
		synchronized (dis) {
			dis.wait(10000);
		}
	}
	
	/**
	 * 
	 * <p>
	 * Wartet auf das Eintreffen einer Nachricht auf irgendeinem Channel.
	 * Vorform eines Selects. Sollten wir Select brauchen wird es später noch
	 * einmal implementiert.
	 * </p>
	 * 
	 * @throws InterruptedException
	 */
	public static void waitForMessage() throws InterruptedException{
		synchronized (UdpChannelFactory.class) {
			UdpChannelFactory.class.wait(10000);
		}
	}

	protected static class MessageDispatcher extends Thread {
		Map<InetSocketAddress, UdpChannel> endpoints;
		DatagramSocket socket;
		Queue<UdpChannel> unconnectedChannels;

		public MessageDispatcher(DatagramSocket socket) {
			this.socket = socket;
			this.endpoints = new HashMap<InetSocketAddress, UdpChannel>();
			this.unconnectedChannels = new LinkedList<UdpChannel>();
		}

		void addUnconnectedChannel(UdpChannel channel) {
			this.unconnectedChannels.add(channel);
		}

		void connectEndpoint(InetAddress endPoint, int port, UdpChannel channel) {
			this.endpoints.put(new InetSocketAddress(endPoint, port), channel);
			channel.connect(endPoint, port);
		}

		@Override
		public void run() {
			super.run();
			byte[] buffer = new byte[512]; // max udp size
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			while (true) {

				try {
					this.socket.receive(incoming);

					InetSocketAddress from = new InetSocketAddress(
							incoming.getAddress(), incoming.getPort());

					if (!this.endpoints.containsKey(from)
							&& this.unconnectedChannels.isEmpty()) {
						continue;
						// Unbekannte Endpoints die keinem Channel zugeordnet
						// werden kÃ¶nnen, werden gedroppt.
					} else if (!this.endpoints.containsKey(from)
							&& !this.unconnectedChannels.isEmpty()) {
						// whos waiting?
						UdpChannel toChannel = this.unconnectedChannels.poll();
						// connect it
						toChannel.connect(from.getAddress(), from.getPort());
						this.endpoints.put(from, toChannel);

					}
					// Put message into queue of channel
					this.endpoints.get(from).recieved.add(new UDPMessage(
							incoming.getData(), incoming.getLength()));
					this.notifyAll();
					UdpChannelFactory.class.notifyAll();
				} catch (IOException e) {
					if (endpoints.isEmpty() && unconnectedChannels.isEmpty()) {
						// exit thread if all channels are closed
						break;
					}
					continue;
				}
			}
		}
	}

}
