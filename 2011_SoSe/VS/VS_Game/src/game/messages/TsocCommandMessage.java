package game.messages;

import java.util.Arrays;

import vsFramework.Channel;
import game.messages.handler.TsocCommandHandler;
import game.networking.GameMessage;

public class TsocCommandMessage extends CommandMessage<TsocCommandHandler>{

	@Override
	public void execute(Channel c, String[] paras)
			throws IllegalArgumentException {
		try{
			String[] way = Arrays.copyOf(paras, paras.length-5);
			String good = paras[paras.length-4];
			int price = Integer.parseInt(paras[paras.length-2]);
			int amount = Integer.parseInt(paras[paras.length-1]);
			for(TsocCommandHandler h : reg){
				h.onTsoc(way, good, price, amount);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public GameMessage message() {
		return GameMessage.TSOC;
	}

}
