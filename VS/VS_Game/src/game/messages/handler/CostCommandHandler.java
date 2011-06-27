package game.messages.handler;

public interface CostCommandHandler extends MessageHandler{
	void onCost(String[] way, String good);
}
