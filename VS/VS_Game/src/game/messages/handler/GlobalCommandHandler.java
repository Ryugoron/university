package game.messages.handler;

import vsFramework.Channel;

public interface GlobalCommandHandler extends Handler{
	void onGlobal(Channel c, String from, String msg, String[] way);
}
