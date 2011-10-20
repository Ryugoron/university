package game.commands;

import game.commands.handler.CostHandler;

public class CostCommand extends Command<CostHandler> {

	@Override
	public void execute(String[] paras) throws IllegalArgumentException {
		try {
			// Parse names
			
			int pos = this.search(paras, ":");
			
			if(pos == -1) throw new IllegalArgumentException();
			
			String name = "";
			for (int i=0; i<pos; ++i){
				name += paras[i];
			}
			
			String planet = "";
			for(int i=pos+1; i<paras.length; ++i){
				planet += paras[i];
			}
			
			for (CostHandler h : this.reg) {
				h.costCommand(name, planet);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String usage() {
		return "cost <planetname> : <goodname>";
	}

	@Override
	public String description() {
		return "askes a planet to tell the costs of a good";
	}

	@Override
	public String command() {
		return "cost";
	}

	
	private int search(String[] in, String s) {
		int found = -1;
		for (int i = 0; i < in.length; ++i) {
			if (in[i].equals(s))
				found = i;
		}
		return found;
	}

}
