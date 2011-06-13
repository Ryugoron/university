package game.messages.handler;

import vsFramework.Channel;

public interface LocalCommandHandler extends Handler{
	void onLocal(Channel c, String from, String msg);
}
