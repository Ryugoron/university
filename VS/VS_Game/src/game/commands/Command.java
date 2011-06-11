package game.commands;

import java.util.ArrayList;
import java.util.List;

import game.commands.handler.Handler;

public abstract class Command<V extends Handler> {
	
	protected List<V> reg = new ArrayList<V>();
	
	public abstract void execute(String[] paras) throws IllegalArgumentException;
	
	public void register(V handler){
		reg.add(handler);
	}
	
	public abstract String usage();
	public abstract String description();
}
