package game.messages.handler;

import vsFramework.Channel;

public interface PeersCommandHandler extends MessageHandler{
	public void onPeers(Channel c, String[] args);
}
