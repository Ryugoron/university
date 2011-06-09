package game;

import game.commands.Command;
import game.commands.ConnectCommand;
import game.commands.PeersCommand;

import java.util.HashMap;
import java.util.Map;

class GameMessageProcessor implements InputHandler {
	final Map<String, Command> commands = new HashMap<String, Command>();
	final Game game;

	GameMessageProcessor(Game game) {
		this.game = game;
		commands.put("connect", new ConnectCommand(game));
		commands.put("peers", new PeersCommand(game));
	}

	@Override
	public void onInput(String input) {
		String[] commandParts = input.split(" ");
		Command command = commands.get(commandParts[0]);
		if (command != null) {
			try {
			command.execute(commandParts);
			} catch (IllegalArgumentException e) {
				game.getConsole().writeLine(command.usage());
			}
			
		} else {
			game.getConsole().writeLine(this.listCommands());
		}
	}

	protected String wrongInput() {
		return null;
	}

	protected String listCommands() {
		StringBuilder sb = new StringBuilder("---------Commands----------\n");
		for (String command : this.commands.keySet()) {
			sb.append(command);
			sb.append("\n");
		}
		sb.append("---------------------------");
		return sb.toString();
	}

}
