package game.messages.handler;

import vsFramework.Channel;

public interface PeersCommandHandler extends Handler{
	public void onPeers(Channel c, String[] args);
}
