package game.messages;

import vsFramework.Channel;
import game.messages.handler.DockCommandHandler;

public class DockCommandMessage extends CommandMessage<DockCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		if(paras.length < 1) throw new IllegalArgumentException("No name inserted.");
		
		for(DockCommandHandler h : reg){
			h.onDock(c,paras[0]);
		}
		
	}

	@Override
	public String message() {
		return "DOCK";
	}

}
