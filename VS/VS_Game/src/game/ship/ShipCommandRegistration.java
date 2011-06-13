package game.ship;

import game.commands.CloseCommand;
import game.commands.ClsCommand;
import game.commands.Command;
import game.commands.DockCommand;
import game.commands.GlobalCommand;
import game.commands.HelpCommand;
import game.commands.LocalCommand;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import console.InputHandler;

public class ShipCommandRegistration implements InputHandler{
	final private Ship ship;
	
	final Map<String, Command<?>> commands = new HashMap<String, Command<?>>();
	
	public ShipCommandRegistration(Ship ship) {
		this.ship = ship;
		addCommandHandler();
	}
	
	private void addCommandHandler() {
		{
			DockCommand h = new DockCommand();
			h.register(ship);
			commands.put(h.command(), h);
		}
		{
			LocalCommand h = new LocalCommand();
			h.register(ship);
			commands.put(h.command(), h);
		}
		{
			GlobalCommand h = new GlobalCommand();
			h.register(ship);
			commands.put(h.command(), h);
		}
		{
			HelpCommand h = new HelpCommand();
			h.register(ship);
			commands.put(h.command(), h);
		}
		{
			ClsCommand h = new ClsCommand();
			h.register(ship);
			commands.put(h.command(), h);
		}
		{
			CloseCommand h = new CloseCommand();
			h.register(ship);
			commands.put(h.command(), h);
		}
	}

	@Override
	public void onInput(String input) {
		String[] commandParts = input.split("\\s+");
		Command<?> command = commands.get(commandParts[0]);
		if (command != null) {
			try {
			command.execute(Arrays.copyOfRange(commandParts, 1, commandParts.length));
			} catch (IllegalArgumentException e) {
				if(e.getMessage() != null){
					if (!e.getMessage().equals("")) {
						this.ship.getConsole().println(e.getMessage());
					}
				}
				this.ship.getConsole().println(command.usage());
			}
			
		} else {
			this.ship.getConsole().println("Unknown Command");
			this.ship.getConsole().println(" >>\"help\"");
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
