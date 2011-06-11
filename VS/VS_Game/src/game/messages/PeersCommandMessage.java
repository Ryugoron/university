package game.messages;

import vsFramework.Channel;
import game.messages.handler.PeersCommandHandler;

public class PeersCommandMessage extends CommandMessage<PeersCommandHandler> {

	@Override
	public void execute(Channel c,String[] paras) throws IllegalArgumentException {
		for(PeersCommandHandler h : reg){
			h.onPeers(c, paras);
		}
		
	}

}
