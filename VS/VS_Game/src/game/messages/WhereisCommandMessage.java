package game.messages;

import vsFramework.Channel;
import game.messages.handler.WhereisCommandHandler;
import game.networking.GameMessage;

public class WhereisCommandMessage extends CommandMessage<WhereisCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String name = paras[1];
			for(int i=2; i<paras.length; ++i){
				name += " "+paras[i];
			}
			for(WhereisCommandHandler h : reg){
				h.onWhereis(c,paras[0],name);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.WHEREIS;
	}
}
