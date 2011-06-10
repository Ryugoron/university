package game.commands;

import game.Game;

public class CloseCommand implements Command{

	final Game game;
	
	public CloseCommand(Game game) {
		this.game = game;
	}
	
	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		game.close();
		
	}

	@Override
	public String usage() {
		return "close";
	}

	@Override
	public String description() {
		return "Closes the window";
	}
	
	

}
