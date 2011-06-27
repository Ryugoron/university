package game.messages.handler;

import vsFramework.Channel;

public interface WhereisCommandHandler extends MessageHandler{
	void onWhereis(Channel c, String ship, String name);
}
