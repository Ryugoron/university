package game.messages.handler;

import vsFramework.Channel;

public interface LlesCommandHandler extends MessageHandler{
	void onLles(Channel c, String name, int amount, int win);
}
