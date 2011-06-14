package game;

import game.commands.CloseCommand;
import game.commands.ClsCommand;
import game.commands.Command;
import game.commands.ConnectCommand;
import game.commands.DockCommand;
import game.commands.GlobalCommand;
import game.commands.HelpCommand;
import game.commands.LocalCommand;
import game.commands.PeersCommand;
import game.commands.handler.CloseHandler;
import game.commands.handler.ClsHandler;
import game.commands.handler.ConnectHandler;
import game.commands.handler.DockHandler;
import game.commands.handler.GlobalHandler;
import game.commands.handler.HelpHandler;
import game.commands.handler.LocalHandler;
import game.commands.handler.PeersHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import console.InputHandler;

public class CommandRegistration implements InputHandler {
	final private Game node;

	final Map<String, Command<?>> commands = new HashMap<String, Command<?>>();

	public CommandRegistration(Game node) {
		this.node = node;
		addCommandHandler();
	}

	private void addCommandHandler() {
		if(node instanceof PeersHandler){
			PeersCommand h = new PeersCommand();
			h.register((PeersHandler)node);
			commands.put(h.command(), h);}
		if(node instanceof ConnectHandler){
			ConnectCommand h = new ConnectCommand();
			h.register((ConnectHandler)node);
			commands.put(h.command(), h);}
		if(node instanceof ClsHandler){
			ClsCommand h = new ClsCommand();
			h.register((ClsHandler)node);
			commands.put(h.command(), h);}
		if(node instanceof HelpHandler){
			HelpCommand h = new HelpCommand();
			h.register((HelpHandler)node);
			commands.put(h.command(), h);}
		if(node instanceof CloseHandler){
			CloseCommand h = new CloseCommand();
			h.register((CloseHandler)node);
			commands.put(h.command(), h);}
		if(node instanceof DockHandler){
			DockCommand h = new DockCommand();
			h.register((DockHandler)node);
			commands.put(h.command(), h);}
		if(node instanceof GlobalHandler){
			GlobalCommand h = new GlobalCommand();
			h.register((GlobalHandler)node);
			commands.put(h.command(), h);}
		if(node instanceof LocalHandler){
			LocalCommand h = new LocalCommand();
			h.register((LocalHandler)node);
			commands.put(h.command(), h);}
	}

	@Override
	public void onInput(String input) {
		String[] commandParts = input.split("\\s+");
		Command<?> command = commands.get(commandParts[0]);
		if (command != null) {
			try {
				command.execute(Arrays.copyOfRange(commandParts, 1,
						commandParts.length));
			} catch (IllegalArgumentException e) {
				if (e.getMessage() != null) {
					if (!e.getMessage().equals("")) {
						this.node.getConsole().println(e.getMessage());
					}
				}
				this.node.getConsole().println(command.usage());
			}

		} else {
			this.node.getConsole().println("Unknown Command");
			this.node.getConsole().println(" >>\"help\"");
		}

	}

	public String helpText() {
		StringBuilder sb = new StringBuilder(
				"\n\n-------- Commands ---------\n");
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
