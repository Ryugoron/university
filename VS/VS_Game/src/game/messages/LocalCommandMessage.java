package game.messages;

import vsFramework.Channel;
import game.messages.handler.LocalCommandHandler;

public class LocalCommandMessage extends CommandMessage<LocalCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		if(paras.length < 2) throw new IllegalArgumentException("No Message ditributed.");
		
		for(LocalCommandHandler h : reg){
			h.onLocal(c,paras[0],paras[1]);
		}
		
	}

	@Override
	public String message() {
		return "LOCAL";
	}

}
