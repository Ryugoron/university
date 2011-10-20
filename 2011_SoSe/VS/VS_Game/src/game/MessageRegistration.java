package game;

import game.messages.CommandMessage;
import game.messages.CostCommandMessage;
import game.messages.DockCommandMessage;
import game.messages.GlobalCommandMessage;
import game.messages.GoodsCommandMessage;
import game.messages.HelloCommandMessage;
import game.messages.KcodCommandMessage;
import game.messages.LocalCommandMessage;
import game.messages.OllehCommandMessage;
import game.messages.PeersCommandMessage;
import game.messages.SdoogCommandMessage;
import game.messages.SreepCommandMessage;
import game.messages.ThereisCommandMessage;
import game.messages.TsocCommandMessage;
import game.messages.UndockCommandMessage;
import game.messages.WhereisCommandMessage;
import game.messages.handler.CostCommandHandler;
import game.messages.handler.DockCommandHandler;
import game.messages.handler.GlobalCommandHandler;
import game.messages.handler.GoodsCommandHandler;
import game.messages.handler.HelloCommandHandler;
import game.messages.handler.KcodCommandHandler;
import game.messages.handler.LocalCommandHandler;
import game.messages.handler.OllehCommandHandler;
import game.messages.handler.PeersCommandHandler;
import game.messages.handler.SdoogCommandHandler;
import game.messages.handler.SreepCommandHandler;
import game.messages.handler.ThereisCommandHandler;
import game.messages.handler.TsocCommandHandler;
import game.messages.handler.UndockCommandHandler;
import game.messages.handler.WhereisCommandHandler;
import game.networking.UdpChannelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vsFramework.Channel;
import vsFramework.Message;

public class MessageRegistration {
final private Game node;

final Map<String, CommandMessage<?>> messages = new HashMap<String, CommandMessage<?>>();

private List<Channel> connectedPeers = new ArrayList<Channel>();
private List<Channel> removedPeers = new ArrayList<Channel>();
private List<Channel> addedPeers = new ArrayList<Channel>();

public final int LOCALPORT;
final int LISTEN_NUM = 5;

private List<Channel> listenChannel = new ArrayList<Channel>();

public MessageRegistration(Game node, int port) {
	this.node = node;
	this.LOCALPORT = port;
	this.fillListenChannel();
	addMessageHandler();
	new Communicaton();
}

private void addMessageHandler() {
	if(this.node instanceof HelloCommandHandler){
		HelloCommandMessage h = new HelloCommandMessage();
		h.register((HelloCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof OllehCommandHandler){
		OllehCommandMessage h = new OllehCommandMessage();
		h.register((OllehCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof PeersCommandHandler){
		PeersCommandMessage h = new PeersCommandMessage();
		h.register((PeersCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof SreepCommandHandler){
		SreepCommandMessage h = new SreepCommandMessage();
		h.register((SreepCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof DockCommandHandler){
		DockCommandMessage h = new DockCommandMessage();
		h.register((DockCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof GlobalCommandHandler){
		GlobalCommandMessage h = new GlobalCommandMessage();
		h.register((GlobalCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof LocalCommandHandler){
		LocalCommandMessage h = new LocalCommandMessage();
		h.register((LocalCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof KcodCommandHandler){
		KcodCommandMessage h = new KcodCommandMessage();
		h.register((KcodCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof GoodsCommandHandler){
		GoodsCommandMessage h = new GoodsCommandMessage();
		h.register((GoodsCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof SdoogCommandHandler){
		SdoogCommandMessage h = new SdoogCommandMessage();
		h.register((SdoogCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof CostCommandHandler){
		CostCommandMessage h = new CostCommandMessage();
		h.register((CostCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof TsocCommandHandler){
		TsocCommandMessage h = new TsocCommandMessage();
		h.register((TsocCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof UndockCommandHandler){
		UndockCommandMessage h = new UndockCommandMessage();
		h.register((UndockCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof ThereisCommandHandler){
		ThereisCommandMessage h = new ThereisCommandMessage();
		h.register((ThereisCommandHandler)node);
		messages.put(h.message().toString(), h);}
	if(this.node instanceof WhereisCommandHandler){
		WhereisCommandMessage h = new WhereisCommandMessage();
		h.register((WhereisCommandHandler)node);
		messages.put(h.message().toString(), h);}
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
	
	private void scanForMessage(Collection<Channel> in){
		for (Channel c : in) {
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
	}

	public void run() {
		while (true) {
			try {
				UdpChannelFactory.waitOnPort(LOCALPORT);
			} catch (InterruptedException e) {
				continue;
			}
			
			this.scanForMessage(listenChannel);
			this.scanForMessage(connectedPeers);
			
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
