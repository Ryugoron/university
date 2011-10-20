package game.messages;

import vsFramework.Channel;
import game.messages.handler.SdoogCommandHandler;
import game.networking.GameMessage;

public class SdoogCommandMessage extends CommandMessage<SdoogCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		for(SdoogCommandHandler h : reg){
			h.onSdoog(c, paras);
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.SDOOG;
	}

}
