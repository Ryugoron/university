package game.commands.handler;

import java.net.InetAddress;

public interface DockHandler extends Handler{
	void onDock(InetAddress host, int port);
}
