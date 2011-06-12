package game.planet;

import game.commands.handler.CloseHandler;
import game.commands.handler.ClsHandler;
import game.commands.handler.ConnectHandler;
import game.commands.handler.HelpHandler;
import game.commands.handler.PeersHandler;
import game.messages.handler.HelloCommandHandler;
import game.messages.handler.OllehCommandHandler;
import game.messages.handler.PeersCommandHandler;
import game.messages.handler.SreepCommandHandler;
import game.networking.GameMessage;
import game.networking.UdpChannelFactory;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vsFramework.Channel;
import console.Console;
import console.Console.StdFd;
import console.planet.PlanetConsole;

public class Planet implements CloseHandler, ClsHandler, ConnectHandler,
		HelpHandler, PeersHandler, HelloCommandHandler, OllehCommandHandler,
		PeersCommandHandler, SreepCommandHandler {

	protected Console con;
	private Map<String, Channel> connectedPeers = new HashMap<String, Channel>();
	private List<Channel> pendingPeers = new LinkedList<Channel>();

	final protected String name;

	final private PlanetCommandRegistration reg;
	final private PlanetMessageRegistration mreg;

	// ------------------- Variables for the Programm it self
	// -----------------------------
	private Map<String, String[]> peersToWork = new HashMap<String, String[]>();
	private Map<String, String[]> peers = new HashMap<String, String[]>();

	// ------------------ Contruct and help Funktions
	// --------------------------------------

	public Planet(int port) {
		reg = new PlanetCommandRegistration(this);
		mreg = new PlanetMessageRegistration(this, port);
		createGUI();
		name = con.waitForName();
		con.setVisible(true);
		con.println("Welcome! You are located at planet \"" + name + "\"");
		con.println("Your current Port is: " + port);
		con.println(StdFd.Planets, "Planetlist: \n\n>> No planets in reach.");
	}

	private void createGUI() {
		con = new PlanetConsole();
		con.setInputHandler(reg);
	}

	Console getConsole() {
		return this.con;
	}

	private void updatePlanetList() {
		// TODO Erweitern sobald Peers fertig ist
		con.clear(StdFd.Planets);
		con.println(StdFd.Planets, "Planetlist:\n\n");
		for (String s : this.peers.keySet()) {
			con.println(StdFd.Planets, " >> " + s);
		}
	}

	// ----------------- Jump Points for Incomming Message ------------------

	public void onHello(Channel c, String name) {
		this.connectedPeers.put(name, c);
		mreg.addPeer(c);
		String[] myName = { this.name };
		c.send(GameMessage.OLLEH.toMessage(myName));
		this.con.println("A new planet was discoverd right next to us.");
		String[] way = { name };
		this.peers.put(name, way);
		this.updatePlanetList();
	}

	public void onOlleh(Channel c, String name) {
		if (!pendingPeers.contains(c))
			return;

		connectedPeers.put(name, c);
		String[] way = { name };
		this.peers.put(name, way);
		pendingPeers.remove(c);
		this.con.println("A new planet was discovered right next to us.");
		this.updatePlanetList();
	}

	public void onPeers(Channel c, String[] inc) {
		synchronized (this) {
			// TODO Problem with endless ending, change to equals iff fixed
			if (inc.length < 2)
				throw new IllegalArgumentException("To small way.");
			if (inc[inc.length-1].startsWith(this.name)) {
				// reverse IT
				String[] sendBuffer = new String[inc.length
						+ this.connectedPeers.size() + 1];
				for (int i = 0; i < inc.length; ++i) {
					sendBuffer[inc.length - i] = inc[i];
				}
				sendBuffer[inc.length] = "#";
				int i = inc.length;
				Channel backChannel; //Unschön, aber mit lineendings muss man aufpassen
				for (String peer : this.connectedPeers.keySet()) {
					sendBuffer[++i] = peer;
					System.out.println("Im Anschluss : "+peer);
				}
				System.out.println(inc[inc.length - 2]);
				this.connectedPeers.get(inc[inc.length - 2]).send(
						GameMessage.SREEP.toMessage(sendBuffer));
			} else {
				// Send it on
				String next = "";
				for (int i = 1; i < inc.length - 1; ++i) {
					if (inc[i].startsWith(this.name)) {
						next = inc[i];
						break;
					}
				}
				if (!this.connectedPeers.containsKey(next))
					throw new IllegalArgumentException("Wrong way buddy");
				this.connectedPeers.get(next).send(
						GameMessage.PEERS.toMessage(inc));
			}
		}
	}

	public void onSreep(Channel c, String[] inc) {
		synchronized (this) {
			if (inc.length < 4)
				throw new IllegalArgumentException("To small way");
			int pos = Arrays.binarySearch(inc, 0, inc.length, "#");
			if (inc[pos - 1].equals(this.name)) {
				// Wir haben das Ziel erreicht!
				String[] newEdges = Arrays
						.copyOfRange(inc, pos + 1, inc.length);
				this.peersToWork.remove(inc[1]);
				// Weg wiederum umdrehen
				String[] way = new String[pos + 1];
				for (int i = 0; i < pos; ++i) {
					way[pos - i] = inc[i];
				}
				//Neue Kanten hinzufügen und ansprechen
				for (int i = 0; i < newEdges.length; ++i) {
					if (this.peersToWork.containsKey(newEdges[i])
							|| this.peers.containsKey(newEdges[i]))
						continue;
					way[pos] = newEdges[i];
					this.peersToWork.put(newEdges[i], way);
					this.peers.put(newEdges[i], way);
					this.connectedPeers.get(way[1]).send(
							GameMessage.PEERS.toMessage(way));
				}
				this.updatePlanetList();
			} else {
				for (int i = 1; i < pos; ++i) {
					if (inc[i].equals(this.name)) {
						// wir haben uns gefunden
						this.connectedPeers.get(inc[i - 1]).send(
								GameMessage.SREEP.toMessage(inc));
					}
				}
			}
		}
	}

	// ----------------- Jump Points for Command Classes ----------------------

	public void onPeers() {
		System.out.println("Peers was invoced");
		synchronized (this) {
			this.peersToWork.clear();
			this.peers.clear();
			
			String[] way = new String[2];
			way[0] = this.name;
			for (String cName : this.connectedPeers.keySet()) {
				way[1] = cName;
				this.peersToWork.put(cName, way);
				this.connectedPeers.get(cName).send(
						GameMessage.PEERS.toMessage(way));
			}
		}
	}

	public void onConnect(InetAddress host, int port) {
		Channel chan = UdpChannelFactory.newUdpChannel(mreg.LOCALPORT, host,
				port);
		pendingPeers.add(chan);
		mreg.addPeer(chan);
		String[] name = new String[1];
		name[0] = this.name;
		chan.send(GameMessage.HELLO.toMessage(name));

		this.con.println("connect executed");
	}

	public void onClose() {
		this.con.println("Bye");
		System.exit(0);
	}

	public void onHelp() {
		this.con.println(this.reg.helpText());
	}

	public void onCls() {
		this.con.clear();
		this.updatePlanetList();
	}
}
