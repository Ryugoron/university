package game;

import game.networking.UdpChannelFactory;

import java.util.LinkedList;
import java.util.List;

import vsFramework.Channel;
import console.GameConsole;

public class Game {
	private Console con;
	private List<Channel> connectedPeers = new LinkedList<Channel>();
	private Channel listenChannel;
	final protected String name;

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
//		listenChannel = UdpChannelFactory.newUdpChannel(4711);
	}
	
	public void peers() {
		this.con.writeLine("Peers ausgeführt");
	}
	
	public void connect(String host, int port) {
		this.con.writeLine("connect ausgeführt");
	}
	
	protected Console getConsole() {
		return this.con;
	}
}
