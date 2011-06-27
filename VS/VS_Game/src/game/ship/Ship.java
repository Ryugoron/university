package game.ship;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import vsFramework.Channel;

import console.Console;
import console.Console.StdFd;
import console.ship.ShipConsole;

import game.CommandRegistration;
import game.Game;
import game.MessageRegistration;
import game.commands.handler.CloseHandler;
import game.commands.handler.ClsHandler;
import game.commands.handler.CostHandler;
import game.commands.handler.DockHandler;
import game.commands.handler.GlobalHandler;
import game.commands.handler.GoodsHandler;
import game.commands.handler.HelpHandler;
import game.commands.handler.LocalHandler;
import game.commands.handler.PeersHandler;
import game.commands.handler.TravelHandler;
import game.messages.handler.GlobalCommandHandler;
import game.messages.handler.KcodCommandHandler;
import game.messages.handler.LocalCommandHandler;
import game.messages.handler.SdoogCommandHandler;
import game.messages.handler.SreepCommandHandler;
import game.messages.handler.ThereisCommandHandler;
import game.messages.handler.TsocCommandHandler;
import game.networking.GameMessage;
import game.networking.UdpChannel;
import game.networking.UdpChannelFactory;

public class Ship implements Game, DockHandler, LocalHandler, GlobalHandler,
		KcodCommandHandler, LocalCommandHandler, HelpHandler, ClsHandler,
		GlobalCommandHandler, CloseHandler, GoodsHandler, SdoogCommandHandler,
		CostHandler, TsocCommandHandler, PeersHandler, SreepCommandHandler, 
		ThereisCommandHandler, TravelHandler {

	protected Console con;

	final protected String name;

	// Wir sind immer nur mit einem Planeten verbuden
	private String pName;
	private Channel pChannel;

	private Map<String, String[]> peersToWork = new HashMap<String, String[]>();
	private Map<String, String[]> peers = new HashMap<String, String[]>();
	
	final private CommandRegistration reg;
	final private MessageRegistration mreg;

	public Ship(int port, String name) {
		reg = new CommandRegistration(this);
		mreg = new MessageRegistration(this, port);
		createGUI();
		this.name = GameMessage.prepareProtokoll(name);
		con.setVisible(true);
		con.println("Welcome! At yout ship \"" + name + "\"");
		this.updatePlanetList();
	}

	public Ship(int port) {
		reg = new CommandRegistration(this);
		mreg = new MessageRegistration(this, port);
		createGUI();
		name = GameMessage.prepareProtokoll(con.waitForName());
		con.setVisible(true);
		con.println("Welcome! At yout ship \"" + name + "\"");
		this.updatePlanetList();
	}

	private void createGUI() {
		con = new ShipConsole();
		con.setInputHandler(reg);
	}

	public Console getConsole() {
		return this.con;
	}
	
	private int search(String[] in, String s) {
		int found = -1;
		for (int i = 0; i < in.length; ++i) {
			if (in[i].equals(s))
				found = i;
		}
		return found;
	}
	
	private String[] copyInTo(String[] in, String[] out) {
		if (in.length > out.length)
			return out;
		for (int i = 0; i < in.length; ++i) {
			out[i] = in[i];
		}
		return out;
	}

	private void updatePlanetList() {
		con.clear(StdFd.Planets);
		con.println(StdFd.Planets, "Planetlist:\n");
		if (this.peers.isEmpty()) {
			con.println(StdFd.Planets, " >> No planets in reach");
		}
		for (String s : this.peers.keySet()) {
			con.println(StdFd.Planets, " >> " + s);
		}
	}
	
	// -------------- Command Jump Points ---------------------
	@Override
	public void onGlobal(String msg) {
		synchronized (this) {
			if (this.pChannel != null) {
				msg = GameMessage.prepareProtokoll(msg);
				String[] buffer = new String[2];
				buffer[0] = this.name;
				buffer[1] = msg;
				pChannel.send(GameMessage.GLOBAL.toMessage(buffer));
				this.con.println("GLOBAL[" + this.name + "]: " + msg);
			}
		}
	}

	@Override
	public void onLocal(String msg) {
		synchronized (this) {
			if (this.pChannel != null) {
				msg = GameMessage.prepareProtokoll(msg);
				String[] buffer = new String[2];
				buffer[0] = this.name;
				buffer[1] = msg;
				pChannel.send(GameMessage.LOCAL.toMessage(buffer));
				this.con.println("LOCAL[" + this.name + "]: " + msg);
			}
		}
	}

	@Override
	public void onDock(InetAddress host, int port) {
		synchronized (this) {
			Channel chan = UdpChannelFactory.newUdpChannel(mreg.LOCALPORT,
					host, port);
			pChannel = chan;
			mreg.addPeer(chan);
			String[] myName = new String[1];
			myName[0] = this.name;
			chan.send(GameMessage.DOCK.toMessage(myName));

			this.con.println("Asked for docking place");
		}

	}

	// ----------- Message Jump Points -----------------

	@Override
	public void onGlobal(Channel c, String from, String msg, String[] way) {
		synchronized (this) {
			this.con.println(StdFd.Messages, GameMessage.GLOBAL.toString()
					+ " " + msg);
			this.con.println("GLOBAL[" + from + "]: " + msg);
		}
	}

	@Override
	public void onLocal(Channel c, String from, String msg) {
		synchronized (this) {
			this.con.println(StdFd.Messages, GameMessage.LOCAL.toString() + " "
					+ msg);
			this.con.println("LOCAL[" + from + "]: " + msg);
		}
	}

	@Override
	public void onKcod(Channel c, String name) {
		synchronized (this) {
			this.con.println(StdFd.Messages, GameMessage.KCOD.toString() + " "
					+ name);
			if (pChannel.equals(c)) {
				this.pName = name;
				this.con.println(">> Docking permitted by planet: "
						+ this.pName);
			}
			String[] way = new String[2];
			way[0] = this.name;
			way[1] = name;
			this.peers.put(name, way);
			this.updatePlanetList();
		}
	}

	@Override
	public void onClose() {
		System.exit(0);
	}

	@Override
	public void onCls() {
		synchronized (this) {
			this.con.clear();
		}

	}

	@Override
	public void onHelp() {
		synchronized (this) {
			this.con.println(this.reg.helpText());
		}

	}

	@Override
	public void goodsCommand() {
		synchronized (this) {
			this.con.println("Asked for goods.\n");
			if (this.pName == null) {
				this.con.println("You have to land on a planet,\n  to use a communication reley.\n");
				return;
			}
			String[] empty = { this.name, "#" };
			this.pChannel.send(GameMessage.GOODS.toMessage(empty));
		}
	}

	@Override
	public void onSdoog(Channel c, String[] goods) {
		String oMessage = GameMessage.SDOOG.toString();
		for (int i = 0; i < goods.length; ++i) {
			oMessage += " " + goods[i];
		}
		this.con.println(StdFd.Messages, oMessage);

		this.con.println("Available Goods: ");
		for (int i = 2; i < goods.length; ++i) {
			String[] split = goods[i].split("\\.");
			this.con.println(" >> " + split[0]);
		}
	}

	@Override
	public void onTsoc(String[] way, String good, int price, int amount) {
		// Nicht machen, einfach weg hauen
		this.con.println("PriceInfo >> "+ good + " [price: "
				+ price + "; amount: " + amount+ "] on planet "+way[0]);
	}

	@Override
	public void costCommand(String name, String good) {
		if(this.peers.containsKey(name)){
			String[] way = new String[this.peers.get(name).length+2];
			way = this.copyInTo(this.peers.get(name), way);
			way[this.peers.get(name).length] = "#";
			way[this.peers.get(name).length+1] = good;
			
			this.pChannel.send(GameMessage.COST.toMessage(way));
		}
	}

	@Override
	public void onSreep(Channel c, String[] inc) {
		synchronized (this) {

			String oMessage = GameMessage.SREEP.toString();
			for (int i = 0; i < inc.length; ++i) {
				oMessage += " " + inc[i];
			}
			this.con.println(StdFd.Messages, oMessage);

			if (inc.length < 4)
				throw new IllegalArgumentException("To small way");
			int pos = this.search(inc, "#");
			if (pos == -1)
				throw new IllegalArgumentException("Error in Message");
			else if (inc[pos - 1].equals(this.name)) {
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
					this.pChannel.send(
							GameMessage.PEERS.toMessage(way));
				}
				this.updatePlanetList();
			}
		}
	}

	@Override
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
			way[1] = this.pName;
			this.peersToWork.put(this.pName, way);
			this.peers.put(this.pName, way);
			this.pChannel.send(
					GameMessage.PEERS.toMessage(way));
		}

	}

	@Override
	public void onThereis(String addr, int port) {
		this.con.println(StdFd.Messages, GameMessage.THEREIS + " "+ addr+ " " + port);
		InetAddress a;
		if(addr.equals("127.0.0.1")){
			a = ((UdpChannel) pChannel).getRemoteAddress();
		}else{
			try {
				a = InetAddress.getByName(addr);
			} catch (UnknownHostException e) {
				this.con.println("Lost connection to the planet.");
				return;
			}
		}
		this.mreg.removePeer(pChannel, pName);
		pChannel.close();
		this.pChannel = null;
		this.pName = null;
		this.peers.clear();
		
		this.onDock(a, port);
	}

	@Override
	public void onTravel(String name) {
		String[] remotename = {this.name , GameMessage.prepareProtokoll(name)};
		this.pChannel.send(GameMessage.WHEREIS.toMessage(remotename));
	}
}
