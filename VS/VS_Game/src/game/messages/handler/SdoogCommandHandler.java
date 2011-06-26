package game.messages.handler;

import vsFramework.Channel;

public interface SdoogCommandHandler extends MessageHandler{
	void onSdoog(Channel c, String[] goods);
}
