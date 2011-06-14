package game.messages.handler;

import vsFramework.Channel;

public interface OllehCommandHandler extends MessageHandler{
	public void onOlleh(Channel c, String name);
}
