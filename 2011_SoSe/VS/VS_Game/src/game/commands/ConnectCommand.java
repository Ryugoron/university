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
			host = InetAddress.getByName(paras[0]);
			port = Integer.parseInt(paras[1]);

			for(ConnectHandler h : reg){
				h.onConnect(host, port);
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Unkown Host");
			throw new IllegalArgumentException("Ungültiger Host");
 		} catch (Exception e) {
 			System.out.println(e.getMessage());
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

	@Override
	public String command() {
		return "connect";
	}


}
