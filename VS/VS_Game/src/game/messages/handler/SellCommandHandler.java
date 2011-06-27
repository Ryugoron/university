package game.messages.handler;

import vsFramework.Channel;

public interface SellCommandHandler extends MessageHandler{
	void onSell(Channel c, String name, int amount);
}
