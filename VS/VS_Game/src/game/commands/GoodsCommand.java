package game.commands;

import game.commands.handler.GoodsHandler;

public class GoodsCommand extends Command<GoodsHandler>{

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String usage() {
		return "goods";
	}

	@Override
	public String description() {
		return "erfragt eine Liste aller bekannt Güter aus dem Universum";
	}

	@Override
	public String command() {
		return "goods";
	}

}
