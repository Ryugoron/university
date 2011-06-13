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
		return "Shows a list of operations with description.";
	}

	@Override
	public String command() {
		return "help";
	}

}
