package game.messages;

import vsFramework.Channel;
import game.messages.handler.LlesCommandHandler;
import game.networking.GameMessage;

public class LlesCommandMessage extends CommandMessage<LlesCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String name = paras[0];
			int amount = Integer.parseInt(paras[1]);
			int win = Integer.parseInt(paras[2]);
			for(LlesCommandHandler h : reg){
				h.onLles(c, name, amount, win);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.LLES;
	}

}
