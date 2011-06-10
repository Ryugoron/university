package game.commands;

import game.Game;

public class ClsCommand implements Command {

	final Game game;
	
	public ClsCommand(Game game){
		this.game = game;
	}
	
	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		this.game.clearScreen();
	}

	@Override
	public String usage() {
		return "cls";
	}

	@Override
	public String description() {
		return "Clears the standard console";
	}

}
