package game.commands;

public interface Command {
	void execute(String[] paras) throws IllegalArgumentException;
	String usage();
	String description();
}
