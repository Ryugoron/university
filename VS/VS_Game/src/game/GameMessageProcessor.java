package game;

class GameMessageProcessor implements InputHandler {
	
	GameMessageProcessor() {
		//TODO: Krams
	}

	@Override
	public String onInput(String input) {
		System.out.println(input);
		return input;
	}

}
