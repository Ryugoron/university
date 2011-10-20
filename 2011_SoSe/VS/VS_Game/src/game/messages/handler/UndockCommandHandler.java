package game.messages.handler;

import vsFramework.Channel;

public interface UndockCommandHandler extends MessageHandler{
	void onUndock(Channel c, String name);
}
