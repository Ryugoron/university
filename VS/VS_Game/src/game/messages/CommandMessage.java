package game.messages;

import game.messages.handler.MessageHandler;
import game.networking.GameMessage;

import java.util.ArrayList;
import java.util.List;

import vsFramework.Channel;

public abstract class CommandMessage<V extends MessageHandler> {
	
protected List<V> reg = new ArrayList<V>();
	
	public abstract void execute(Channel c, String[] paras) throws IllegalArgumentException;
	
	public void register(V handler){
		reg.add(handler);
	}
	
	public abstract GameMessage message();
}
