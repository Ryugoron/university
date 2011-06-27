package game.commands;

import game.commands.handler.SellHandler;

public class SellCommand extends Command<SellHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		try{
			int amount = Integer.parseInt(paras[0]);
			String name = paras[1];
			for(int i=2; i<paras.length; ++i){
				name += " "+paras[i];
			}
			for(SellHandler h : reg){
				h.onSell(name, amount);
			}
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "sell <amount> <name>";
	}

	@Override
	public String description() {
		return "sells an amount of goods on the planet your located now.";
	}

	@Override
	public String command() {
		return "sell";
	}

}
