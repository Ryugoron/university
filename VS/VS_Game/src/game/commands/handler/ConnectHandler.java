package game.commands.handler;

import java.net.InetAddress;

public interface ConnectHandler extends Handler{
	void onConnect(InetAddress host, int port);
}
