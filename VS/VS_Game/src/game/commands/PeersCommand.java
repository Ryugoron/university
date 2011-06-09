package game.commands;

import game.Game;

public class PeersCommand implements Command {
	final Game game;
	
	
	public PeersCommand(Game game) {
		this.game = game;
	}

	@Override
	public void execute(String[] paras) {
		game.peers();
	}

	@Override
	public String usage() {
		return "peers";
	}

}
