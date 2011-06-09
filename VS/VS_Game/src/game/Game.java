package game;

import console.GameConsole;

public class Game {
	public Game() {
		Console con = new GameConsole();
		con.setInputHandler(new GameMessageProcessor());
		con.setVisible(true);
	}
}
