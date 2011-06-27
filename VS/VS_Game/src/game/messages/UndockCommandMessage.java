package game.messages;

import vsFramework.Channel;
import game.messages.handler.UndockCommandHandler;
import game.networking.GameMessage;

public class UndockCommandMessage extends CommandMessage<UndockCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String name = paras[0];
			for(int i=1; i<paras.length; ++i){
				name += " "+paras[i];
			}
			for(UndockCommandHandler h : reg){
				h.onUndock(c, name);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.UNDOCK;
	}

}
