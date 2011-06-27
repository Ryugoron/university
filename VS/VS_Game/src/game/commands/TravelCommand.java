package game.commands;

import game.commands.handler.TravelHandler;

public class TravelCommand extends Command<TravelHandler> {

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		try{
		for(TravelHandler h : reg){
			String name = paras[0];
			for(int i = 1; i<paras.length; ++i){
				name += " "+paras[i];
			}
			h.onTravel(name);
		}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "travel <planetname>";
	}

	@Override
	public String description() {
		return "flies to a planet near by.";
	}

	@Override
	public String command() {
		return "travel";
	}
	
}
