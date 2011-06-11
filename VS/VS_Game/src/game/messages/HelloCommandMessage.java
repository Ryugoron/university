package game.messages;

import vsFramework.Channel;
import game.messages.handler.HelloCommandHandler;

public class HelloCommandMessage extends CommandMessage<HelloCommandHandler> {

	@Override
	public void execute(Channel c, String[] paras) throws IllegalArgumentException {
		if(paras.length < 1) throw new IllegalArgumentException("No name inserted.");
		
		for(HelloCommandHandler h : reg){
			h.onHello(c,paras[0]);
		}
	}

}
