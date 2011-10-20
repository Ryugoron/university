package game.messages.handler;

import vsFramework.Channel;

public interface HelloCommandHandler extends MessageHandler {
	public void onHello(Channel c, String name);
}
