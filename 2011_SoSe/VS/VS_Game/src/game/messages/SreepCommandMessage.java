package game.messages;

import vsFramework.Channel;
import game.messages.handler.SreepCommandHandler;
import game.networking.GameMessage;

public class SreepCommandMessage extends CommandMessage<SreepCommandHandler> {

	@Override
	public void execute(Channel c, String[] paras) throws IllegalArgumentException {
		for(SreepCommandHandler h : reg){
			h.onSreep(c, paras);
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.SREEP;
	}

}
