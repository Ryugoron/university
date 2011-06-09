package game.commands;

import game.Game;

public class ConnectCommand implements Command {
	final Game game;
	
	public ConnectCommand(Game game) {
		this.game = game;
	}
	
	
	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		String host;
		int port;
		try {
			host = paras[1];
			port = Integer.parseInt(paras[2]);
			game.connect(host, port);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "connect <host: IP or DNS> <port: Int>";
	}


}
