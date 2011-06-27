package game.messages.handler;

import vsFramework.Channel;

public interface YubCommandHandler extends MessageHandler{
	void onYub(Channel c, String name, int amount, int cost);
}
