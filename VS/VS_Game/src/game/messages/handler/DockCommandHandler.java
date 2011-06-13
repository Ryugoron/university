package game.messages.handler;

import vsFramework.Channel;

public interface DockCommandHandler extends Handler{
	void onDock(Channel c, String name);
}
