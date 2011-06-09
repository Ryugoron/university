package game.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import game.Game;

public class ConnectCommand implements Command {
	final Game game;
	
	public ConnectCommand(Game game) {
		this.game = game;
	}
	
	
	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		InetAddress host;
		int port;
		try {
			host = InetAddress.getByName(paras[1]);
			port = Integer.parseInt(paras[2]);
			game.connect(host, port);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Ungültiger Host");
 		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "connect <host: IP or DNS> <port: Int>";
	}


}
