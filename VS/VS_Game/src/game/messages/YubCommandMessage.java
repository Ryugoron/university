package game.messages;

import vsFramework.Channel;
import game.messages.handler.YubCommandHandler;
import game.networking.GameMessage;

public class YubCommandMessage extends CommandMessage<YubCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String name = paras[0];
			int amount = Integer.parseInt(paras[1]);
			int cost = Integer.parseInt(paras[2]);
			for(YubCommandHandler h : reg){
				h.onYub(c, name, amount, cost);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.YUB;
	}

}
