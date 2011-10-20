package game.messages.handler;

public interface TsocCommandHandler extends MessageHandler{
	void onTsoc(String way[], String good, int price, int amount);
}
