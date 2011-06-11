package game.messages.handler;

import vsFramework.Channel;

public interface SreepCommandHandler extends Handler {
	public void onSreep(Channel c, String[] args);
}
