package game;

import game.networking.GameMessage;
import game.networking.UdpChannelFactory;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import vsFramework.Channel;
import console.GameConsole;

public class Game {
	private Console con;
	private List<Channel> connectedPeers = new LinkedList<Channel>();
	private List<Channel> pendingPeers = new LinkedList<Channel>();
	private Channel listenChannel;
	final protected String name;
	final private int LOCALPORT = 4711;

	public Game() {
		createGUI();
		createListenChannel();
		con.setVisible(true);
		name = con.waitForName();
		con.writeLine("Welcome! You are located at planet "+name);
	}

	private void createGUI() {
		con = new GameConsole();
		con.setInputHandler(new GameMessageProcessor(this));
	}

	private void createListenChannel() {
//		listenChannel = UdpChannelFactory.newUdpChannel(LOCALPORT);
	}
	
	public void peers() {
		// TODO: Implement peers
		this.con.writeLine("Peers ausgeführt");
	}
	
	public void connect(InetAddress host, int port) {
		// TODO: Implement connect
		Channel chan = UdpChannelFactory.newUdpChannel(LOCALPORT, host, port);
		pendingPeers.add(chan);
		chan.send(GameMessage.HELLO);
		
		this.con.writeLine("connect ausgeführt");
	}
	
	protected Console getConsole() {
		return this.con;
	}
}
