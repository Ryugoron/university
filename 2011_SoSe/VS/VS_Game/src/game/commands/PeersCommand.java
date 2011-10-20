package game.commands;

import game.commands.handler.PeersHandler;

public class PeersCommand extends Command<PeersHandler> {

	@Override
	public void execute(String[] paras) {
		for(PeersHandler h : reg){
			h.onPeers();
		}
	}

	@Override
	public String usage() {
		return "peers";
	}
	
	@Override
	public String description() {
		return "Returns a list of any reachable planet.";
	}

	@Override
	public String command() {
		return "peers";
	}
	
	
}
