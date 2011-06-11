package game.messages.handler;

import vsFramework.Channel;

public interface OllehCommandHandler extends Handler{
	public void onOlleh(Channel c, String name);
}
