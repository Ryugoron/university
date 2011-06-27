package game.commands;

import game.commands.handler.BuyHandler;

public class BuyCommand extends Command<BuyHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		try{
			int amount = Integer.parseInt(paras[0]);
			String name = paras[1];
			for(int i=2; i<paras.length; ++i){
				name += " "+paras[i];
			}
			for(BuyHandler h : reg){
				h.onBuy(name, amount);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "buy <amount> <goodname>";
	}

	@Override
	public String description() {
		return "buys a good on the planet your located now";
	}

	@Override
	public String command() {
		return "buy";
	}

}
