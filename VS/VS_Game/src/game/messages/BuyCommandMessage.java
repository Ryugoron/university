package game.messages;

import vsFramework.Channel;
import game.messages.handler.BuyCommandHandler;
import game.networking.GameMessage;

public class BuyCommandMessage extends CommandMessage<BuyCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String name = paras[0];
			int amount = Integer.parseInt(paras[1]);
			for(BuyCommandHandler h : reg){
				h.onBuy(c, name, amount);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.BUY;
	}

}
