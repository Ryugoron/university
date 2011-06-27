package game.commands.handler;

public interface BuyHandler extends CommandHandler{
	void onBuy(String name, int amount);
}
