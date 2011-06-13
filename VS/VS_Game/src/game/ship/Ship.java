package game.ship;

import java.net.InetAddress;

import vsFramework.Channel;

import console.Console;
import console.ship.ShipConsole;

import game.commands.handler.CloseHandler;
import game.commands.handler.ClsHandler;
import game.commands.handler.DockHandler;
import game.commands.handler.GlobalHandler;
import game.commands.handler.HelpHandler;
import game.commands.handler.LocalHandler;
import game.messages.handler.GlobalCommandHandler;
import game.messages.handler.KcodCommandHandler;
import game.messages.handler.LocalCommandHandler;
import game.networking.GameMessage;
import game.networking.UdpChannelFactory;

public class Ship implements DockHandler, LocalHandler, GlobalHandler,
		KcodCommandHandler, LocalCommandHandler, HelpHandler, ClsHandler,
		GlobalCommandHandler, CloseHandler {
	
	protected Console con;
	
	final protected String name;
	
	//Wir sind immer nur mit einem Planeten verbuden
	private String pName;
	private Channel pChannel;
	
	final private ShipCommandRegistration reg;
	final private ShipMessageRegistration mreg;

	
	
	public Ship(int port, String name) {
		reg = new ShipCommandRegistration(this);
		mreg = new ShipMessageRegistration(this, port);
		createGUI();
		this.name = GameMessage.prepareProtokoll(name);
		con.setVisible(true);
		con.println("Welcome! At yout ship \"" + name + "\"");
	}

	public Ship(int port) {
		reg = new ShipCommandRegistration(this);
		mreg = new ShipMessageRegistration(this, port);
		createGUI();
		name = GameMessage.prepareProtokoll(con.waitForName());
		con.setVisible(true);
		con.println("Welcome! At yout ship \"" + name + "\"");
	}
	
	private void createGUI() {
		con = new ShipConsole();
		con.setInputHandler(reg);
	}
	
	public Console getConsole() {
		return this.con;
	}
	
	
//-------------- Command Jump Points ---------------------
	@Override
	public void onGlobal(String msg) {
		synchronized (this) {
			if(this.pChannel != null){
				msg = GameMessage.prepareProtokoll(msg);
				String[] buffer = new String[2];
				buffer[0] = this.name;
				buffer[1] = msg;
				pChannel.send(GameMessage.GLOBAL.toMessage(buffer));
				this.con.println("GLOBAL["+this.name+"]: "+msg);
			}
		}
	}

	@Override
	public void onLocal(String msg) {
		synchronized (this) {
			if(this.pChannel != null){
				msg = GameMessage.prepareProtokoll(msg);
				String[] buffer = new String[2];
				buffer[0] = this.name;
				buffer[1] = msg;
				pChannel.send(GameMessage.LOCAL.toMessage(buffer));
				this.con.println("LOCAL["+this.name+"]: "+msg);
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
			String[] name = new String[1];
			name[0] = this.name;
			chan.send(GameMessage.DOCK.toMessage(name));

			this.con.println("Asked for docking place");
		}

	}

	
//----------- Message Jump Points -----------------
	
	@Override
	public void onGlobal(Channel c, String from, String msg, String[] way) {
		this.con.println("GLOBAL["+from+"]: "+msg);
	}

	@Override
	public void onLocal(Channel c, String from, String msg) {
		this.con.println("LOCAL["+from+"]: "+msg);
	}

	@Override
	public void onKcod(Channel c, String name) {
		synchronized (this) {
			if(pChannel.equals(c)){
				this.pName = name;
				this.con.println(">> Docking permitted by planet: "+this.pName);
			}
		}
	}

	@Override
	public void onClose() {
		System.exit(0);
	}

	@Override
	public void onCls() {
		this.con.clear();
		
	}

	@Override
	public void onHelp() {
		this.con.println(this.reg.helpText());
		
	}
}
