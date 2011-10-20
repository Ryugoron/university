package game.commands;

import game.commands.handler.CloseHandler;

public class CloseCommand extends Command<CloseHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		for(CloseHandler h : reg){
			h.onClose();
		}
		
	}

	@Override
	public String usage() {
		return "close";
	}

	@Override
	public String description() {
		return "Closes the window";
	}

	@Override
	public String command() {
		return "close";
	}
	
}
