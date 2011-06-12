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

	// ------------------- Variables for the Programm it self ---------------

	private Map<String, String[]> peersToWork = new HashMap<String, String[]>();
	private Map<String, String[]> peers = new HashMap<String, String[]>();

	// ------------------ Contruct and help Funktions ----------------------
	
	public Planet(int port, String name){
		reg = new PlanetCommandRegistration(this);
		mreg = new PlanetMessageRegistration(this, port);
		createGUI();
		this.name = prepare(name);
		con.setVisible(true);
		con.println("Welcome! You are located at planet \"" + name + "\"");
		con.println("Your current StarGate is: " + port);
		con.println(StdFd.Planets, "Planetlist: \n\n>> No planets in reach.");
	}
	
	public Planet(int port) {
		reg = new PlanetCommandRegistration(this);
		mreg = new PlanetMessageRegistration(this, port);
		createGUI();
		name = con.waitForName();
		con.setVisible(true);
		con.println("Welcome! You are located at planet \"" + name + "\"");
		con.println("Your current StarGate is: " + port);
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
		con.clear(StdFd.Planets);
		con.println(StdFd.Planets, "Planetlist:\n\n");
		for (String s : this.peers.keySet()) {
			con.println(StdFd.Planets, " >> " + s);
		}
	}
	
	private String prepare(String in){
		in = in.replaceAll("%", "%25");
		in = in.replaceAll("\\s", "%20");
		return in;
	}

	@SuppressWarnings("unused")
	private String[] invert(String[] in) {
		String sp;
		for (int i = 0; i < (in.length / 2); ++i) {
			sp = in[i];
			in[i] = in[in.length - 1 - i];
			in[in.length - 1 - i] = sp;
		}
		return in;
	}

	private String[] invertInTo(String[] in, String[] out) {
		if (in.length > out.length)
			return out;
		for (int i = 0; i < in.length; ++i) {
			out[i] = in[in.length - 1 - i];
		}
		return out;
	}

	private int search(String[] in, String s) {
		int found = -1;
		for (int i = 0; i < in.length; ++i) {
			if (in[i].equals(s))
				found = i;
		}
		return found;
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
		this.con.println(StdFd.Messages, "HELLO " + name);
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
		this.con.println(StdFd.Messages, "OLLEH " + name);
		this.updatePlanetList();
	}

	public void onPeers(Channel c, String[] inc) {
		synchronized (this) {
			if (inc.length < 2)
				throw new IllegalArgumentException("To small way.");
			if (inc[inc.length - 1].equals(this.name)) {
				// reverse IT
				String[] sendBuffer = new String[inc.length
						+ this.connectedPeers.size() + 1];

				this.invertInTo(inc, sendBuffer);
				sendBuffer[inc.length] = "#";
				int i = inc.length;
				for (String peer : this.connectedPeers.keySet()) {
					sendBuffer[++i] = peer;
				}
				this.connectedPeers.get(inc[inc.length - 2]).send(
						GameMessage.SREEP.toMessage(sendBuffer));
			} else {
				// Send it on
				String next = "";
				for (int i = 1; i < inc.length - 1; ++i) {
					if (inc[i].equals(this.name)) {
						next = inc[i + 1];
						break;
					}
				}
				if (!this.connectedPeers.containsKey(next))
					throw new IllegalArgumentException("From " + this.name
							+ " you can't reach " + next);
				this.connectedPeers.get(next).send(
						GameMessage.PEERS.toMessage(inc));
			}

			// Output
			String oMessage = "PEERS";
			for (int i = 0; i < inc.length; ++i) {
				oMessage += " " + inc[i];
			}
			this.con.println(StdFd.Messages, oMessage);
		}
	}

	public void onSreep(Channel c, String[] inc) {
		synchronized (this) {
			if (inc.length < 4)
				throw new IllegalArgumentException("To small way");
			int pos = this.search(inc, "#");
			if (inc[pos - 1].equals(this.name)) {
				// Wir haben das Ziel erreicht!
				String[] newEdges = Arrays
						.copyOfRange(inc, pos + 1, inc.length);
				this.peersToWork.remove(inc[0]);

				// Weg wiederum umdrehen
				String[] way = new String[pos + 1];
				for (int i = 0; i < pos; ++i) {
					way[(pos - 1) - i] = inc[i];
				}
				// Neue Kanten hinzufügen und ansprechen
				for (int i = 0; i < newEdges.length; ++i) {
					if (this.peersToWork.containsKey(newEdges[i])
							|| this.peers.containsKey(newEdges[i])
							|| newEdges[i].equals(this.name))
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
						this.connectedPeers.get(inc[i + 1]).send(
								GameMessage.SREEP.toMessage(inc));
					}
				}
			}

			String oMessage = "SREEP";
			for (int i = 0; i < inc.length; ++i) {
				oMessage += " " + inc[i];
			}
			this.con.println(StdFd.Messages, oMessage);
		}
	}

	// ----------------- Jump Points for Command Classes ----------------------

	public void onPeers() {
		synchronized (this) {
			this.peersToWork.clear();
			this.peers.clear();

			this.con.println("We started a misson\n    "
					+ "to explore strange new worlds,\n    "
					+ "to seek out new life and new civilizations,\n    "
					+ "to boldly go where no one has gone before!!!");

			String[] way = new String[2];
			way[0] = this.name;
			for (String cName : this.connectedPeers.keySet()) {
				way[1] = cName;
				this.peersToWork.put(cName, way);
				this.peers.put(cName, way);
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
		this.con.clear(StdFd.Messages);
		this.updatePlanetList();
	}
}
