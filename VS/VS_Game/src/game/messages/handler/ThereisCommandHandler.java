package game.messages.handler;

public interface ThereisCommandHandler extends MessageHandler{
	void onThereis(String addr, int port);
}
