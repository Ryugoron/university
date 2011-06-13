package game.messages;

import vsFramework.Channel;
import game.messages.handler.DockCommandHandler;
import game.networking.GameMessage;

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
	public GameMessage message() {
		return GameMessage.DOCK;
	}

}
