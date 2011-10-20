package game.commands;

import game.commands.handler.ClsHandler;

public class ClsCommand extends Command<ClsHandler> {

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		for(ClsHandler h : reg){
			h.onCls();
		}
	}

	@Override
	public String usage() {
		return "cls";
	}

	@Override
	public String description() {
		return "Clears the standard console";
	}

	@Override
	public String command() {
		return "cls";
	}

}
