package game.commands;

import java.util.ArrayList;
import java.util.List;

import game.commands.handler.CommandHandler;

public abstract class Command<V extends CommandHandler> {
	
	protected List<V> reg = new ArrayList<V>();
	
	public abstract void execute(String[] paras) throws IllegalArgumentException;
	
	public void register(V handler){
		reg.add(handler);
	}
	
	public abstract String usage();
	public abstract String description();
	
	public abstract String command();
}
