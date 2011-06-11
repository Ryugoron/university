package game.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import game.commands.handler.ConnectHandler;

public class ConnectCommand extends Command<ConnectHandler> {
	
	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		InetAddress host;
		int port;
		try {
			host = InetAddress.getByName(paras[1]);
			port = Integer.parseInt(paras[2]);

			for(ConnectHandler h : reg){
				h.onConnect(host, port);
			}
			
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Ungültiger Host");
 		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "connect <host: IP or DNS> <port: Int>";
	}


	@Override
	public String description() {
		return "Connect to another planet. Loses the old connection.";
	}


}
