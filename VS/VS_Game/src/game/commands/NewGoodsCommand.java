package game.commands;

import game.commands.handler.NewGoodHandler;

public class NewGoodsCommand extends Command<NewGoodHandler> {

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		try {
			int ttl = Integer.parseInt(paras[0]);
			int price = Integer.parseInt(paras[1]);
			int need = Integer.parseInt(paras[2]);
			int amount = Integer.parseInt(paras[3]);
			String name = paras[4];
			for(int i=5; i<paras.length; ++i){
				name += " "+paras[i];
			}
			
			for (NewGoodHandler h : this.reg) {
				h.onNewGood(name, ttl, need, amount, price);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "newGood <ttl> <price> <need> <amount> <name>";
	}

	@Override
	public String description() {
		return "erstellt eine neue Ware, die auf diesem Planeten gehandelt wird.";
	}

	@Override
	public String command() {
		return "newGood";
	}

}
