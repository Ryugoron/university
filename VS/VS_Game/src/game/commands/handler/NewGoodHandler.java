package game.commands.handler;

public interface NewGoodHandler extends CommandHandler{
	void onNewGood(String name, int ttl, int need, int amount, int price);
}
