package game.commands;

import game.commands.handler.StatusHandler;

public class StatusCommand extends Command<StatusHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		for(StatusHandler h : reg){
			h.onStatus();
		}
	}

	@Override
	public String usage() {
		return "status";
	}

	@Override
	public String description() {
		return "returns a list of goods in you ship\n and the total amount of gold.";
	}

	@Override
	public String command() {
		return "status";
	}

}
