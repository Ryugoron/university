package game.messages.handler;

import vsFramework.Channel;

public interface KcodCommandHandler extends Handler{
	void onKcod(Channel c, String name);
}
