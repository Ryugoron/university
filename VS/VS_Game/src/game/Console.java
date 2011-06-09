package game;

public interface Console {
	void setInputHandler(InputHandler handler);

	void setVisible(boolean visible);
	String waitForName();
	
	void writeLine(String text);
}
