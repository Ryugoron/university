package game.messages;

import vsFramework.Channel;
import game.messages.handler.ThereisCommandHandler;
import game.networking.GameMessage;

public class ThereisCommandMessage extends CommandMessage<ThereisCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			for(ThereisCommandHandler h : reg){
				h.onThereis(paras[0], Integer.parseInt(paras[1]));
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.THEREIS;
	}

}
