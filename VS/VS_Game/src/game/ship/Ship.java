package game.ship;

import java.net.InetAddress;

import vsFramework.Channel;

import console.Console;
import console.Console.StdFd;
import console.ship.ShipConsole;

import game.CommandRegistration;
import game.Game;
import game.MessageRegistration;
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

public class Ship implements Game, DockHandler, LocalHandler, GlobalHandler,
		KcodCommandHandler, LocalCommandHandler, HelpHandler, ClsHandler,
		GlobalCommandHandler, CloseHandler {
	
	protected Console con;
	
	final protected String name;
	
	//Wir sind immer nur mit einem Planeten verbuden
	private String pName;
	private Channel pChannel;
	
	final private CommandRegistration reg;
	final private MessageRegistration mreg;

	
	
	public Ship(int port, String name) {
		reg = new CommandRegistration(this);
		mreg = new MessageRegistration(this, port);
		createGUI();
		this.name = GameMessage.prepareProtokoll(name);
		con.setVisible(true);
		con.println("Welcome! At yout ship \"" + name + "\"");
	}

	public Ship(int port) {
		reg = new CommandRegistration(this);
		mreg = new MessageRegistration(this, port);
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
			String[] myName = new String[1];
			myName[0] = this.name;
			chan.send(GameMessage.DOCK.toMessage(myName));

			this.con.println("Asked for docking place");
		}

	}

	
//----------- Message Jump Points -----------------
	
	@Override
	public void onGlobal(Channel c, String from, String msg, String[] way) {
		synchronized (this) {
			this.con.println(StdFd.Messages, GameMessage.GLOBAL.toString() + " "+ msg);
			this.con.println("GLOBAL["+from+"]: "+msg);
		}
	}

	@Override
	public void onLocal(Channel c, String from, String msg) {
		synchronized (this) {
			this.con.println(StdFd.Messages, GameMessage.LOCAL.toString() + " "+ msg);
			this.con.println("LOCAL["+from+"]: "+msg);
		}
	}

	@Override
	public void onKcod(Channel c, String name) {
		synchronized (this) {
			this.con.println(StdFd.Messages, GameMessage.KCOD.toString() + " "+ name);
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
}
