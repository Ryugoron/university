package game.messages;

import vsFramework.Channel;
import game.messages.handler.SellCommandHandler;
import game.networking.GameMessage;

public class SellCommandMessage extends CommandMessage<SellCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String name = paras[0];
			int amount = Integer.parseInt(paras[1]);
			for(SellCommandHandler h : reg){
				h.onSell(c, name, amount);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.SELL;
	}

}
