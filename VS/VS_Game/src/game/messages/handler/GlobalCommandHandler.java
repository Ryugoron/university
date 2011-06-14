package game.messages.handler;

import vsFramework.Channel;

public interface GlobalCommandHandler extends MessageHandler{
	void onGlobal(Channel c, String from, String msg, String[] way);
}
