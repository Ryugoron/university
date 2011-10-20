package game.commands.handler;

import java.net.InetAddress;

public interface DockHandler extends CommandHandler{
	void onDock(InetAddress host, int port);
}
