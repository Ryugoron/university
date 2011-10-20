package game.messages;

import java.util.Arrays;

import vsFramework.Channel;
import game.messages.handler.CostCommandHandler;
import game.networking.GameMessage;

public class CostCommandMessage extends CommandMessage<CostCommandHandler> {

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String[] way = Arrays.copyOf(paras, paras.length-2);
			String good = paras[paras.length-1];
		for(CostCommandHandler h : reg){
			h.onCost(way, good);
		}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.COST;
	}

}
