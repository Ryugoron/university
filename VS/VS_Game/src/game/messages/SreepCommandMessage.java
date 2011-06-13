package game.messages;

import vsFramework.Channel;
import game.messages.handler.SreepCommandHandler;

public class SreepCommandMessage extends CommandMessage<SreepCommandHandler> {

	@Override
	public void execute(Channel c, String[] paras) throws IllegalArgumentException {
		for(SreepCommandHandler h : reg){
			h.onSreep(c, paras);
		}
	}

	@Override
	public String message() {
		return "SREEP";
	}

}
