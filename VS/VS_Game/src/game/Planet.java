package game;

import game.Console.StdFd;
import game.commands.handler.CloseHandler;
import game.commands.handler.ClsHandler;
import game.commands.handler.ConnectHandler;
import game.commands.handler.HelpHandler;
import game.commands.handler.PeersHandler;
import game.networking.GameMessage;
import game.networking.UDPMessage;
import game.networking.UdpChannelFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vsFramework.Channel;
import vsFramework.Message;
import console.GameConsole;

public class Planet implements CloseHandler, ClsHandler, ConnectHandler, HelpHandler, PeersHandler{
	protected Console con;
	protected Map<Channel,String> connectedPeers = new HashMap<Channel,String>();
	private List<Channel> pendingPeers = new LinkedList<Channel>();
	private Channel listenChannel;
	
	final protected String name;
	final private int LOCALPORT = 4711;
	
	final private PlanetRegistration reg;
	
	private int silly = 0;
	
	public Planet() {
		reg = new PlanetRegistration(this);
		createGUI();
		name = con.waitForName();
		con.setVisible(true);
		con.println("Welcome! You are located at planet \"" + name + "\"");
		con.println(StdFd.Planets, "Planetlist: \n\n>> No planets in reach.");
	}

	private void createGUI() {
		con = new GameConsole();
		con.setInputHandler(reg);
	}
	
	Console getConsole(){
		return this.con;
	}
	
// ----------------- Handle new Connections ------------------------------
	
//Ich mach ersteinmal alles per Hand, aber Commandpattern wär auch an dieser Stelle gut	
	
protected class Communicaton extends Thread{
	Message actMessage;
	
	public Communicaton(){
		listenChannel = UdpChannelFactory.newUdpChannel(LOCALPORT);
		this.start();
	}
	
	public void run(){
		while(true){
			try {
				UdpChannelFactory.waitOnPort(LOCALPORT);
			} catch (InterruptedException e) {
				continue;
			}
			if((actMessage = listenChannel.nrecv()) != null){
				String[] m = actMessage.getData().toString().split(" ");
				if(m.length != 2) continue;
				if(m[0].equals(GameMessage.HELLO.toString())){
					connectedPeers.put(listenChannel,m[1]);
					listenChannel.send(new UDPMessage((GameMessage.OLLEH.toString()+" "+name).getBytes()));
					listenChannel = UdpChannelFactory.newUdpChannel(LOCALPORT);
				}
				//Wenn nicht verwerfen
				for(Channel c : connectedPeers.keySet()){
					if((actMessage = c.nrecv()) != null){
						onMessage(c,actMessage);
					}
				}
			}
		}
	}
	
}

// ----------------- Incomming Message Handl -----------------------------

public void onMessage(Channel c, Message m){
	String[] inc = m.getData().toString().split(" ");
	if(inc.length == 0) return;
	
	if(inc[1].equals(GameMessage.HELLO)){
		
	}else if(inc[1].equals(GameMessage.OLLEH)){
		
	}else if(inc[1].equals(GameMessage.PEERS)){
		
	}else if(inc[1].equals(GameMessage.SREEP)){
		
	}
}

// ----------------- Jump Points for Incomming Message ------------------

public void onHello(Channel c, String[] inc){
	//Unnützer Versuch, denn wir müssten ja schon connected sein, aber in Ordnung
	
	connectedPeers.put(c, inc[2]);
	c.send(new UDPMessage((GameMessage.OLLEH.toString()+" "+name).getBytes()));
}

public void onOlleh(Channel c, String[] inc){
	connectedPeers.put(c, inc[2]);
}

public void onPeers(Channel c, String[] inc){
	// TODO
}

public void onSreep(Channel c, String[] inc){
	// TODO
}

// ----------------- Jump Points for Command Classes ----------------------
	
	public void onPeers() {
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
				con.println("ok, Ok, OK\nHere are your damn planets.\nHave fun with them.");
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

	public void onConnect(InetAddress host, int port) {
		Channel chan = UdpChannelFactory.newUdpChannel(LOCALPORT, host, port);
		pendingPeers.add(chan);
		chan.send(GameMessage.HELLO);
		
		this.con.println("connect ausgefÃ¼hrt");
	}
	
	public void onClose(){
		this.con.println("Bye");
		System.exit(0);
	}
	
	public void onHelp(){
		this.con.println(this.reg.helpText());
	}
	
	public void onCls(){
		this.con.clear();
	}
}
