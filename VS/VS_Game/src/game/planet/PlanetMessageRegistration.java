package game.planet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.messages.CommandMessage;
import game.messages.DockCommandMessage;
import game.messages.GlobalCommandMessage;
import game.messages.HelloCommandMessage;
import game.messages.LocalCommandMessage;
import game.messages.OllehCommandMessage;
import game.messages.PeersCommandMessage;
import game.messages.SreepCommandMessage;
import game.networking.UdpChannelFactory;
import vsFramework.Channel;
import vsFramework.Message;

public class PlanetMessageRegistration {

	final private Planet planet;

	final Map<String, CommandMessage<?>> messages = new HashMap<String, CommandMessage<?>>();

	private List<Channel> connectedPeers = new ArrayList<Channel>();
	private List<Channel> removedPeers = new ArrayList<Channel>();
	private List<Channel> addedPeers = new ArrayList<Channel>();

	final int LOCALPORT;
	final int LISTEN_NUM = 5;

	private List<Channel> listenChannel = new ArrayList<Channel>();

	public PlanetMessageRegistration(Planet planet, int port) {
		this.planet = planet;
		this.LOCALPORT = port;
		this.fillListenChannel();
		addMessageHandler();
		new Communicaton();
	}

	private void addMessageHandler() {
		{
			HelloCommandMessage h = new HelloCommandMessage();
			h.register(planet);
			messages.put(h.message().toString(), h);
		}
		{
			OllehCommandMessage h = new OllehCommandMessage();
			h.register(planet);
			messages.put(h.message().toString(), h);
		}
		{
			PeersCommandMessage h = new PeersCommandMessage();
			h.register(planet);
			messages.put(h.message().toString(), h);
		}
		{
			SreepCommandMessage h = new SreepCommandMessage();
			h.register(planet);
			messages.put(h.message().toString(), h);
		}
		{
			DockCommandMessage h = new DockCommandMessage();
			h.register(planet);
			messages.put(h.message().toString(), h);
		}
		{
			GlobalCommandMessage h = new GlobalCommandMessage();
			h.register(planet);
			messages.put(h.message().toString(), h);
		}
		{
			LocalCommandMessage h = new LocalCommandMessage();
			h.register(planet);
			messages.put(h.message().toString(), h);
		}
	}

	private void fillListenChannel() {
		synchronized (listenChannel) {
			while (listenChannel.size() < LISTEN_NUM) {
				listenChannel.add(UdpChannelFactory.newUdpChannel(LOCALPORT));
			}
		}
	}

	public void addPeer(Channel c) {
		synchronized (addedPeers) {
			addedPeers.add(c);
		}
	}

	public void removePeer(Channel c, String name) {
		synchronized (removedPeers) {
			removedPeers.add(c);
		}
	}

	// ----------------- Handle Connections ------------------------------

	protected class Communicaton extends Thread {
		Message actMessage;

		public Communicaton() {
			this.start();
		}

		public void run() {
			while (true) {
				try {
					UdpChannelFactory.waitOnPort(LOCALPORT);
				} catch (InterruptedException e) {
					continue;
				}
				for (Channel c : listenChannel) {
					// Wir behandln den "neuen" Channel erst einmal
					// gesondert
					try {
						if ((actMessage = c.nrecv()) != null) {
							String[] input = new String(Arrays.copyOfRange(
									actMessage.getData(), 0,
									actMessage.getLength())).split(" ");
							if (messages.containsKey(input[0]))
								messages.get(input[0]).execute(
										c,
										Arrays.copyOfRange(input, 1,
												input.length));
						}
					} catch (IllegalArgumentException e) {
						if (e.getMessage() != null) {
							if (!e.getMessage().equals("")) {
								System.err.println(e.getMessage());
							}
						}
						System.err.println("Received Unknown Message");
					}
				}

				// TODO besseren syncMechanismus ausdenken, das kopieren ist
				// dumm
				for (Channel c : connectedPeers) {
					try {
						if ((actMessage = c.nrecv()) != null) {
							String[] input = new String(Arrays.copyOfRange(
									actMessage.getData(), 0,
									actMessage.getLength())).split(" ");
							if (messages.containsKey(input[0]))
								messages.get(input[0]).execute(
										c,
										Arrays.copyOfRange(input, 1,
												input.length));
						}
					} catch (IllegalArgumentException e) {
						if (e.getMessage() != null) {
							if (!e.getMessage().equals("")) {
								System.err.println(e.getMessage());
							}
						}
						System.err.println("Received Unknown Message");
					}
				}
				// Wenn wir uns nicht auf die Nachricht registriert haben,
				// verwerfen wir sie

				// Nachsehen, ob sich etwas an den Channels getan hat
				synchronized (addedPeers) {
					connectedPeers.addAll(addedPeers);
					listenChannel.removeAll(addedPeers);
					fillListenChannel();
					addedPeers.clear();
				}
				synchronized (removedPeers) {
					connectedPeers.removeAll(removedPeers);
					removedPeers.clear();
				}
			}
		}
	}
}
