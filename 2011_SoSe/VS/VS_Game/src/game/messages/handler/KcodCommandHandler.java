package game.messages.handler;

import vsFramework.Channel;

public interface KcodCommandHandler extends MessageHandler{
	void onKcod(Channel c, String name);
}
