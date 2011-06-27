package game.commands;

import game.commands.handler.PathHandler;

public class PathCommand extends Command<PathHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		try{
			for(PathHandler h : reg){
				h.onPath(paras[0]);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "path <planetname>";
	}

	@Override
	public String description() {
		return "gives a route to the given planet";
	}

	@Override
	public String command() {
		return "path";
	}

}
