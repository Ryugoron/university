package game.messages;

import game.messages.handler.KcodCommandHandler;
import vsFramework.Channel;

public class KcodCommandMessage extends CommandMessage<KcodCommandHandler> {

	@Override
	public void execute(Channel c,String[] paras) throws IllegalArgumentException {
		if(paras.length < 1) throw new IllegalArgumentException("No name inserted.");
		
		for(KcodCommandHandler h : reg){
			h.onKcod(c,paras[0]);
		}
		
	}

	@Override
	public String message() {
		return "KCOD";
	}

}