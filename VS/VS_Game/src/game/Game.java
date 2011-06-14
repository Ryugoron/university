package game;

import game.commands.handler.CommandHandler;
import game.messages.handler.MessageHandler;
import console.Console;

public interface Game extends CommandHandler, MessageHandler{
	public Console getConsole();
}
