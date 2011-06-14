package game.messages.handler;

import vsFramework.Channel;

public interface DockCommandHandler extends MessageHandler{
	void onDock(Channel c, String name);
}
