package game.messages.handler;

import vsFramework.Channel;

public interface HelloCommandHandler extends Handler {
	public void onHello(Channel c, String name);
}
