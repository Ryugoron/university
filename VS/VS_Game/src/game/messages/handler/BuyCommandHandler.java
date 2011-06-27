package game.messages.handler;

import vsFramework.Channel;

public interface BuyCommandHandler extends MessageHandler{
	void onBuy(Channel c, String name, int amount);
}
