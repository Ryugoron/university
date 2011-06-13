package game.messages;

import java.util.Arrays;

import vsFramework.Channel;
import game.messages.handler.GlobalCommandHandler;

public class GlobalCommandMessage extends CommandMessage<GlobalCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		if(paras.length < 2) throw new IllegalArgumentException("No message distributed.");
		
		for(GlobalCommandHandler h : reg){
			h.onGlobal(c,paras[0],paras[1] , Arrays.copyOfRange(paras, 2, paras.length));
		}
		
	}

	@Override
	public String message() {
		return "GLOBAL";
	}

}
