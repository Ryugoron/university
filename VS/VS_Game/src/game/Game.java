package game;

import game.networking.UdpChannelFactory;

import java.util.LinkedList;
import java.util.List;

import vsFramework.Channel;
import console.GameConsole;

public class Game {
	protected Console con;
	protected List<Channel> connectedPeers = new LinkedList<Channel>();
	protected Channel listenChannel;
	final String name;

	public Game() {
		createGUI();
		createListenChannel();
		con.setVisible(true);
		name = con.waitForName();
		con.writeLine("Welcome! You are located at planet "+name);
	}

	private void createGUI() {
		con = new GameConsole();
		con.setInputHandler(new GameMessageProcessor());
	}

	private void createListenChannel() {
//		listenChannel = UdpChannelFactory.newUdpChannel(4711);
	}
}
