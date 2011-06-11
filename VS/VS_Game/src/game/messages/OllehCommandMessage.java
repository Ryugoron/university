package game.messages;

import vsFramework.Channel;
import game.messages.handler.OllehCommandHandler;

public class OllehCommandMessage extends CommandMessage<OllehCommandHandler> {

	@Override
	public void execute(Channel c,String[] paras) throws IllegalArgumentException {
		if(paras.length < 1) throw new IllegalArgumentException("No name inserted.");
		
		for(OllehCommandHandler h : reg){
			h.onOlleh(c,paras[0]);
		}
		
	}

}
