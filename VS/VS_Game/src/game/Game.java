package game;

import game.Console.StdFd;

import java.util.LinkedList;
import java.util.List;

import vsFramework.Channel;
import console.GameConsole;

public class Game implements InputHandler {
	protected Console con;
	protected List<Channel> connectedPeers = new LinkedList<Channel>();
	protected Channel listenChannel;
	final String name;

	private int silly = 0;
	
	public Game() {
		createGUI();
		createListenChannel();
		name = con.waitForName();
		con.setVisible(true);
		con.println("Welcome! You are located at planet \"" + name + "\"");
		con.println(StdFd.Planets, "Planetlist: \n\n>> No planets in reach.");
	}

	private void createGUI() {
		con = new GameConsole();
		con.setInputHandler(this);
	}

	private void createListenChannel() {
		// listenChannel = UdpChannelFactory.newUdpChannel(4711);
	}

	@Override
	public void onInput(String input) {
		con.println("> " + input);
		if (input.equals("bye")) {
			System.exit(0);
		} else if (input.equals("peers")) {
			this.searchPlanets();
		} else if (input.startsWith("connect")) {
			this.connect(input);
		} else {
			con.println(input);
		}
	}

	private void searchPlanets() {
		switch(this.silly){
			case 0 :
				con.println("No planets found.");
				++silly;
				break;
			case 1 :
				con.clear(StdFd.Planets);
				con.println(StdFd.Planets, "No planets found here either.");
				++silly;
				break;
			case 2 :
				con.println("STOP IT.");
				++silly;
				break;
			case 3 :
				con.println("If yout don't ...");
				++silly;
				break;
			case 4 :
				con.clear();
				con.clear(StdFd.Planets);
				con.println("ok, Ok, OK\nHere are your damn planets.\nHave fund with them.");
				con.println(StdFd.Planets, "Planetlist: \n\n");
				con.println(StdFd.Planets, "Alpha Centauri");
				con.println(StdFd.Planets, "Beta Geuze");
				con.println(StdFd.Planets, "Mars");
				con.println(StdFd.Planets, "Snickers");
				con.println(StdFd.Planets, "Kinderriegel");
				++silly;
				break;
			default :
				con.println("I don't talk to you anymore.\n...\nGo away!");
				break;
		}
	}

	private void connect(String who) {
		con.println("Sry, but we didn't invent the stargate yet.\nPlease try again in thousend years.\n\nDrop Dead.");
	}
}
