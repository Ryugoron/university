package game.planet;

import game.CommandRegistration;
import game.Game;
import game.MessageRegistration;
import game.commands.handler.CloseHandler;
import game.commands.handler.ClsHandler;
import game.commands.handler.ConnectHandler;
import game.commands.handler.GoodsHandler;
import game.commands.handler.HelpHandler;
import game.commands.handler.NewGoodHandler;
import game.commands.handler.PeersHandler;
import game.help.Market;
import game.help.Timer;
import game.help.TimerHandler;
import game.messages.handler.CostCommandHandler;
import game.messages.handler.DockCommandHandler;
import game.messages.handler.GlobalCommandHandler;
import game.messages.handler.GoodsCommandHandler;
import game.messages.handler.HelloCommandHandler;
import game.messages.handler.LocalCommandHandler;
import game.messages.handler.OllehCommandHandler;
import game.messages.handler.PeersCommandHandler;
import game.messages.handler.SdoogCommandHandler;
import game.messages.handler.SreepCommandHandler;
import game.messages.handler.TsocCommandHandler;
import game.messages.handler.UndockCommandHandler;
import game.messages.handler.WhereisCommandHandler;
import game.networking.GameMessage;
import game.networking.UdpChannel;
import game.networking.UdpChannelFactory;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import vsFramework.Channel;
import vsFramework.Message;
import console.Console;
import console.Console.StdFd;
import console.planet.PlanetConsole;

public class Planet implements Game, CloseHandler, ClsHandler, ConnectHandler,
		HelpHandler, PeersHandler, HelloCommandHandler, OllehCommandHandler,
		DockCommandHandler, PeersCommandHandler, SreepCommandHandler,
		GoodsCommandHandler, SdoogCommandHandler, TimerHandler, NewGoodHandler,
		GlobalCommandHandler, LocalCommandHandler, GoodsHandler,
		CostCommandHandler, TsocCommandHandler, UndockCommandHandler, WhereisCommandHandler {

	protected Console con;
	private Map<String, Channel> connectedPeers = new HashMap<String, Channel>();
	private List<Channel> pendingPeers = new LinkedList<Channel>();

	private Map<String, Channel> dockedShips = new HashMap<String, Channel>();

	final protected String name;
	final protected Market market;
	protected Map<String, Integer> rechableGoods = new HashMap<String, Integer>();
	protected boolean askedGoods = false;

	final private CommandRegistration reg;
	final private MessageRegistration mreg;

	// ------------------- Variables for the Programm it self ---------------

	private Map<String, String[]> peersToWork = new HashMap<String, String[]>();
	private Map<String, String[]> peers = new HashMap<String, String[]>();

	// ------------------ Contruct and help Funktions ----------------------

	public Planet(int port, String name) {
		reg = new CommandRegistration(this);
		mreg = new MessageRegistration(this, port);
		market = new Market(0);
		createGUI();
		this.name = GameMessage.prepareProtokoll(name);
		con.setVisible(true);
		con.println("Welcome! You are located at planet \"" + name + "\"");
		con.println("Your current StarGate is: " + port);
		this.updatePlanetList();
		this.con.println(StdFd.Messages, "Messages: \n");
		new Timer(10000, this);
	}

	public Planet(int port) {
		reg = new CommandRegistration(this);
		mreg = new MessageRegistration(this, port);
		market = new Market(0);
		createGUI();
		name = GameMessage.prepareProtokoll(con.waitForName());
		con.setVisible(true);
		con.println("Welcome! You are located at planet \"" + name + "\"");
		con.println("Your current StarGate is: " + port);
		this.updatePlanetList();
		this.con.println(StdFd.Messages, "Messages: \n");
		new Timer(10000, this);
	}

	private void createGUI() {
		con = new PlanetConsole();
		con.setInputHandler(reg);
	}

	@Override
	public Console getConsole() {
		return this.con;
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
		con.println(StdFd.Planets, "\n\nShiplist:\n");
		if (this.dockedShips.isEmpty()) {
			con.println(StdFd.Planets, " >> No ships in orbit");
		}
		for (String s : this.dockedShips.keySet()) {
			con.println(StdFd.Planets, " >> " + s);
		}
		con.println(StdFd.Planets, "\n\nReachable Goods\n");
		for (String s : this.rechableGoods.keySet()) {
			con.println(StdFd.Planets, " >> " + s + "\n");
		}
		for (String s : this.market.allGoods()) {
			con.println(StdFd.Planets, " >> " + s + "\n");
		}
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

	private void distributeMessage(GameMessage m, String from, String msg) {
		for (String send : this.dockedShips.keySet()) {
			if (send.equals(from))
				continue;
			String[] sendBuffer = { from, msg };
			this.dockedShips.get(send).send(m.toMessage(sendBuffer));
		}
	}

	private void updateReachableGoods(String[] goods) {
		for (int i = 2; i < goods.length; ++i) {
			String[] help = goods[i].split("\\.");
			String name = help[0];
			int ttl = Integer.parseInt(help[1]);
			if (!this.rechableGoods.containsKey(name)
					&& !this.market.isGood(name)) {
				this.rechableGoods.put(name, ttl);
			} else if (!this.rechableGoods.containsKey(name)
					&& this.market.isGood(name)) {
				if (this.market.ttl(name) < ttl) {
					this.rechableGoods.put(name, ttl);
				}
			} else {
				if (this.rechableGoods.get(name) < ttl) {
					this.rechableGoods.put(name, ttl);
				}
			}
		}
	}

	private List<String> sendGoods() {
		// Baue den String auf
		List<String> goods = new LinkedList<String>();
		goods.add(this.name);
		goods.add("#");
		for (String name : this.rechableGoods.keySet()) {
			goods.add(name + "." + this.rechableGoods.get(name));
		}
		for (String name : this.market.allGoods()) {
			goods.add(name + "." + this.market.ttl(name));
		}
		return goods;
	}

	// ----------------- Jump Points for Incomming Message ------------------

	public void onHello(Channel c, String name) {
		synchronized (this) {
			if (this.name.equals(name))
				return; // Noch was sinnvolles überlegen
			this.connectedPeers.put(name, c);
			mreg.addPeer(c);
			String[] myName = { this.name };
			c.send(GameMessage.OLLEH.toMessage(myName));
			this.con.println("A new planet was discoverd right next to us.");
			String[] way = { this.name, name };
			this.peers.put(name, way);
			this.con.println(StdFd.Messages, GameMessage.HELLO.toString() + " "
					+ name);
			this.updatePlanetList();
		}
	}

	public void onOlleh(Channel c, String name) {
		synchronized (this) {
			if (!pendingPeers.contains(c))
				return;

			connectedPeers.put(name, c);
			String[] way = { this.name, name };
			this.peers.put(name, way);
			pendingPeers.remove(c);
			this.con.println("A new planet was discovered right next to us.");
			this.con.println(StdFd.Messages, GameMessage.OLLEH.toString() + " "
					+ name);
			this.updatePlanetList();
		}
	}

	public void onPeers(Channel c, String[] inc) {
		synchronized (this) {
			// Output
			String oMessage = GameMessage.PEERS.toString();
			for (int i = 0; i < inc.length; ++i) {
				oMessage += " " + inc[i];
			}
			this.con.println(StdFd.Messages, oMessage);

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
				if (this.dockedShips.containsKey(inc[inc.length - 2])) {
					this.dockedShips.get(inc[inc.length - 2]).send(
							GameMessage.SREEP.toMessage(sendBuffer));
				} else {
					this.connectedPeers.get(inc[inc.length - 2]).send(
							GameMessage.SREEP.toMessage(sendBuffer));
				}
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

		}
	}

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
			if (this.dockedShips.containsKey(inc[pos - 1])) {
				this.dockedShips.get(inc[pos - 1]).send(
						GameMessage.SREEP.toMessage(inc));
			} else if (inc[pos - 1].equals(this.name)) {
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
				String next = "";
				for (int i = 1; i < pos; ++i) {
					if (inc[i].equals(this.name)) {
						// wir haben uns gefunden
						next = inc[i + 1];
						break;
					}
				}
				this.connectedPeers.get(next).send(
						GameMessage.SREEP.toMessage(inc));
			}
		}
	}

	@Override
	public void onDock(Channel c, String oName) {
		synchronized (this) {
			this.dockedShips.put(oName, c);
			mreg.addPeer(c);
			String[] myName = { this.name };
			c.send(GameMessage.KCOD.toMessage(myName));

			this.con.println("A new ship has entered the hangar.");
			this.con.println(StdFd.Messages, GameMessage.DOCK.toString() + " "
					+ name);
			this.updatePlanetList();
		}

	}

	@Override
	public void onLocal(Channel c, String from, String msg) {
		synchronized (this) {
			String oMessage = GameMessage.LOCAL + " " + from + " " + msg;
			this.con.println(StdFd.Messages, oMessage);
			this.distributeMessage(GameMessage.LOCAL, from, msg);
		}

	}

	@Override
	public void onGlobal(Channel c, String from, String msg, String[] way) {
		synchronized (this) {
			String oMessage = GameMessage.GLOBAL + " " + from + " " + msg;
			for (int i = 0; i < way.length; ++i) {
				oMessage += " " + way[i];
			}
			this.con.println(StdFd.Messages, oMessage);

			if (this.dockedShips.containsKey(from)) {
				// Wir müssen die Nachricht verteilen
				for (String next : this.peers.keySet()) {
					String[] sendBuffer = new String[2 + this.peers.get(next).length];
					sendBuffer[0] = from;
					sendBuffer[1] = msg;
					for (int i = 0; i < this.peers.get(next).length; ++i) {
						sendBuffer[i + 2] = this.peers.get(next)[i];
					}
					this.connectedPeers.get(sendBuffer[3]).send(
							GameMessage.GLOBAL.toMessage(sendBuffer));
				}
				// Und an unsere Schiffe schicken
				this.distributeMessage(GameMessage.GLOBAL, from, msg);
			} else {
				if (way.length < 2)
					return; // Falscher Weg
				if (way[way.length - 1].equals(this.name)) {
					// Wir sind die letzten auf dem Weg -> Ausgeben
					this.distributeMessage(GameMessage.GLOBAL, from, msg);
				} else {
					// Wir müssen die Nachricht weiter senden
					String next = "";
					for (int i = 1; i < way.length; ++i) {
						if (way[i].equals(this.name)) {
							// wir haben uns gefunden
							next = way[i + 1];
							break;
						}
					}
					String[] sendBuffer = new String[way.length + 2];
					sendBuffer[0] = from;
					sendBuffer[1] = msg;
					for (int i = 0; i < way.length; ++i) {
						sendBuffer[i + 2] = way[i];
					}
					this.connectedPeers.get(next).send(
							GameMessage.GLOBAL.toMessage(sendBuffer));
				}
			}
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
		synchronized (this) {
			Channel chan = UdpChannelFactory.newUdpChannel(mreg.LOCALPORT,
					host, port);
			pendingPeers.add(chan);
			mreg.addPeer(chan);
			String[] name = new String[1];
			name[0] = this.name;
			chan.send(GameMessage.HELLO.toMessage(name));

			this.con.println("connect executed");
		}
	}

	public void onClose() {
		this.con.println("Bye");
		System.exit(0);
	}

	public void onHelp() {
		this.con.println(this.reg.helpText());
	}

	public void onCls() {
		synchronized (this) {
			this.con.clear();
			this.con.clear(StdFd.Messages);
			this.con.println(StdFd.Messages, "Messages: \n");
			this.updatePlanetList();
		}
	}

	@Override
	public void onSdoog(Channel c, String[] goods) {
		String oMessage = GameMessage.SDOOG.toString();
		for (int i = 0; i < goods.length; ++i) {
			oMessage += " " + goods[i];
		}
		this.con.println(StdFd.Messages, oMessage);

		this.updateReachableGoods(goods);
		this.updatePlanetList();
	}

	@Override
	public void onGoods(Channel c, String[] goods) {
		String oMessage = GameMessage.GOODS.toString();
		for (int i = 0; i < goods.length; ++i) {
			oMessage += " " + goods[i];
		}
		this.con.println(StdFd.Messages, oMessage);

		this.updateReachableGoods(goods);
		c.send(GameMessage.SDOOG.toMessage(this.sendGoods()));
	}

	@Override
	public void onTick() {
		Message m = GameMessage.GOODS.toMessage(this.sendGoods());
		for (Channel c : this.connectedPeers.values()) {
			c.send(m);
		}

	}

	@Override
	public void goodsCommand() {
		this.askedGoods = true;
		Message m = GameMessage.GOODS.toMessage(this.sendGoods());
		for (Channel c : this.connectedPeers.values()) {
			c.send(m);
		}
	}

	@Override
	public void onNewGood(String name, int ttl, int need, int amount, int price) {
		this.market.newGood(GameMessage.prepareProtokoll(name), price, need,
				ttl);
		this.market.setGoodAmount(GameMessage.prepareProtokoll(name), amount);

		this.updatePlanetList();
	}

	@Override
	public void onTsoc(String[] way, String good, int price, int amount) {
		String oMessage = GameMessage.SDOOG.toString();
		for (int i = 0; i < way.length; ++i) {
			oMessage += " " + way[i];
		}
		oMessage += " # "+good+" # "+price+" "+amount;
		this.con.println(StdFd.Messages, oMessage);
		
		if (way[way.length - 2].equals(this.name)) {
			way = this.invertInTo(way, new String[way.length + 5]);

			way[way.length] = "#";
			way[way.length + 1] = good;
			way[way.length + 2] = "#";
			way[way.length + 3] = price + "";
			way[way.length + 1] = amount + "";

			Message m = GameMessage.TSOC.toMessage(way);

			this.dockedShips.get(way[way.length - 1]).send(m);
		} else {
			int pos = this.search(way, this.name);
			if (pos >= 0) {
				way = this.invertInTo(way, new String[way.length + 5]);

				way[way.length] = "#";
				way[way.length + 1] = good;
				way[way.length + 2] = "#";
				way[way.length + 3] = price + "";
				way[way.length + 1] = amount + "";

				Message m = GameMessage.COST.toMessage(way);
				this.connectedPeers.get(way[pos + 1]).send(m);
			}
		}
	}

	@Override
	public void onCost(String[] way, String good) {
		String oMessage = GameMessage.COST.toString();
		for (int i = 0; i < way.length; ++i) {
			oMessage += " " + way[i];
		}
		oMessage += " # "+good;
		
		this.con.println(StdFd.Messages, oMessage);
		
		if (way[way.length - 1].equals(this.name)) {
			// Wir sind die angefragten

			if (this.market.isGood(good)) {
				int price = this.market.price(good);
				int amount = this.market.amount(good);
				
				String[] msg = new String[way.length + 5];
				msg = this.invertInTo(way, msg);
				
				msg[way.length] = "#";
				msg[way.length + 1] = good;
				msg[way.length + 2] = "#";
				msg[way.length + 3] = price + "";
				msg[way.length + 4] = amount + "";
				
				Message m = GameMessage.TSOC.toMessage(msg);
				if(this.dockedShips.containsKey(msg[1])){
					this.dockedShips.get(msg[1]).send(m);
				}else{
					this.connectedPeers.get(msg[1]).send(m);
				}
			}
		} else {
			int pos = this.search(way, this.name);
			if (pos >= 0) {
				way = this.invertInTo(new String[way.length + 2], way);
				way[way.length] = "#";
				way[way.length + 1] = good;

				Message m = GameMessage.COST.toMessage(way);
				this.connectedPeers.get(way[pos + 1]).send(m);
			}
		}
	}

	@Override
	public void onWhereis(Channel c, String ship, String name) {
		this.con.println(StdFd.Messages, GameMessage.WHEREIS+" "+name);
		
		Channel that = this.connectedPeers.get(name);
		if(that == null) return;
		UdpChannel thatU = (UdpChannel) that;
		InetAddress inet = thatU.getRemoteAddress();
		int port = thatU.getRemotePort();
		
		String[] msg = new String[2];
		msg[0] = inet.getHostAddress();
		msg[1] = port+"";
		
		this.dockedShips.remove(ship);
		this.mreg.removePeer(c, ship);
		c.close();
		
		this.updatePlanetList();
		
		c.send(GameMessage.THEREIS.toMessage(msg));
	}

	@Override
	public void onUndock(Channel c, String name) {
		this.dockedShips.remove(name);
		this.updatePlanetList();
	}
}
