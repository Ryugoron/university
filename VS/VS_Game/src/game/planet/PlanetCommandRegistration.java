package game.planet;

import game.commands.CloseCommand;
import game.commands.ClsCommand;
import game.commands.Command;
import game.commands.ConnectCommand;
import game.commands.HelpCommand;
import game.commands.PeersCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import console.InputHandler;

public class PlanetCommandRegistration implements InputHandler {
	final private Planet planet;

	final Map<String, Command<?>> commands = new HashMap<String, Command<?>>();

	public PlanetCommandRegistration(Planet planet) {
		this.planet = planet;
		addCommandHandler();
	}

	private void addCommandHandler() {
		{
			PeersCommand h = new PeersCommand();
			h.register(planet);
			commands.put(h.command(), h);
		}
		{
			ConnectCommand h = new ConnectCommand();
			h.register(planet);
			commands.put(h.command(), h);
		}
		{
			ClsCommand h = new ClsCommand();
			h.register(planet);
			commands.put(h.command(), h);
		}
		{
			HelpCommand h = new HelpCommand();
			h.register(planet);
			commands.put(h.command(), h);
		}
		{
			CloseCommand h = new CloseCommand();
			h.register(planet);
			commands.put(h.command(), h);
		}
	}

	@Override
	public void onInput(String input) {
		String[] commandParts = input.split(" ");
		Command<?> command = commands.get(commandParts[0]);
		if (command != null) {
			try {
			command.execute(Arrays.copyOfRange(commandParts, 1, commandParts.length));
			} catch (IllegalArgumentException e) {
				if(e.getMessage() != null){
					if (!e.getMessage().equals("")) {
						this.planet.getConsole().println(e.getMessage());
					}
				}
				this.planet.getConsole().println(command.usage());
			}
			
		} else {
			this.planet.getConsole().println("Unknown Command");
			this.planet.getConsole().println(" >>\"help\"");
		}

	}
	
	String helpText(){
		StringBuilder sb = new StringBuilder("\n\n-------- Commands ---------\n");
		for (String command : this.commands.keySet()) {
			sb.append(command);
			sb.append(" - ");
			sb.append(this.commands.get(command).description());
			sb.append("\n");
			sb.append("   ->");
			sb.append(this.commands.get(command).usage());
			sb.append("\n");
		}
		sb.append("---------------------------");
		return sb.toString();
	}
}
