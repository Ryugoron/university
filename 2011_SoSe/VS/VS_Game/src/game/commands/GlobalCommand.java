package game.commands;

import game.commands.handler.GlobalHandler;

public class GlobalCommand extends Command<GlobalHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		if(paras.length<1) throw new IllegalArgumentException("Chatmessage contains no message");
		String msg = paras[0];
		for(int i=1; i<paras.length; ++i){
			msg += " " + paras[i];
		}
		for(GlobalHandler h : reg){
			h.onGlobal(msg);
		}
	}

	@Override
	public String usage() {
		return "global <text>";
	}

	@Override
	public String description() {
		return  "Distributes a message over all connected planets.\n" +
				"    This simulates a global chatroom.";
	}

	@Override
	public String command() {
		return "global";
	}

	
}
