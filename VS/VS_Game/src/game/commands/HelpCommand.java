package game.commands;

import game.Game;

public class HelpCommand implements Command{

	final Game game;
	
	public HelpCommand(Game game) {
		this.game = game;
	}
	
	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		this.game.help();
	}

	@Override
	public String usage() {
		return "help";
	}
	
	@Override
	public String description() {
		return "Shows a List off Operations with description.";
	}

}
