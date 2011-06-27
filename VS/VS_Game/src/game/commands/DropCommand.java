package game.commands;

import game.commands.handler.DropHandler;

public class DropCommand extends Command<DropHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		try{
			String name = paras[0];
			for(int i=1; i<paras.length; ++i){
				name += " "+paras[i];
			}
			for(DropHandler h : reg){
				h.onDrop(name);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "drop <goodname>";
	}

	@Override
	public String description() {
		return "droppes a good out of the ship.";
	}

	@Override
	public String command() {
		return "drop";
	}

}
