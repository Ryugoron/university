package game.commands.handler;

import java.net.InetAddress;

public interface ConnectHandler extends CommandHandler{
	void onConnect(InetAddress host, int port);
}
