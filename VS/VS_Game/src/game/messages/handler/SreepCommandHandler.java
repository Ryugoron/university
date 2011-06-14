package game.messages.handler;

import vsFramework.Channel;

public interface SreepCommandHandler extends MessageHandler {
	public void onSreep(Channel c, String[] args);
}
