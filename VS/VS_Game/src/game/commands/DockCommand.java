package game.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import game.commands.handler.DockHandler;

public class DockCommand extends Command<DockHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		InetAddress host;
		int port;
		try {
			host = InetAddress.getByName(paras[0]);
			port = Integer.parseInt(paras[1]);

			for(DockHandler h : reg){
				h.onDock(host, port);
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
		return "dock <host: IP or DNS> <port: Int>";
	}

	@Override
	public String description() {
		return "Docks the ship to a planet";
	}

	@Override
	public String command() {
		return "dock";
	}

}
