package game.commands;

import game.commands.handler.LocalHandler;

public class LocalCommand extends Command<LocalHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		if(paras.length<1) throw new IllegalArgumentException("Chatmessage contains no message");
		String msg = paras[0];
		for(int i=1; i<paras.length; ++i){
			msg += " " + paras[i];
		}
		for(LocalHandler h : reg){
			h.onLocal(msg);
		}
		
	}

	@Override
	public String usage() {
		return "local <text>";
	}

	@Override
	public String description() {
		return  "Distributes a a text message on the local planet.\n" +
				"    This simulates a local chatroom.";
	}

	@Override
	public String command() {
		return "local";
	}

}
