package game.commands.handler;

public interface SellHandler extends CommandHandler{
	void onSell(String name, int amount);
}
