package game.commands;

import game.commands.handler.HelpHandler;

public class HelpCommand extends Command<HelpHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		for(HelpHandler h : reg){
			h.onHelp();
		}
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
