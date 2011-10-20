package game.messages;

import vsFramework.Channel;
import game.messages.handler.GoodsCommandHandler;
import game.networking.GameMessage;

public class GoodsCommandMessage extends CommandMessage<GoodsCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		for(GoodsCommandHandler h : reg){
			h.onGoods(c, paras);
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.GOODS;
	}

}
