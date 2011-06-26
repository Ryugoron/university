package game.messages.handler;

import vsFramework.Channel;

public interface GoodsCommandHandler extends MessageHandler{
	void onGoods(Channel c, String[] goods);
}
