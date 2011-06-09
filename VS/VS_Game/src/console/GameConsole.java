package console;

import game.Console;
import game.InputHandler;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GameConsole extends JFrame implements Console, ActionListener {
	private static final long serialVersionUID = 2404403907190763384L;
	private final static String newline = "\n";

	private JTextArea consoleArea = new JTextArea(5, 20);
	private JTextField consoleInput = new JTextField();
	private JScrollPane scrollPane = new JScrollPane(consoleArea);
	private InputHandler inputHandler;

	public GameConsole() {
		super("Space BWL");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setLayout(new BorderLayout());
		this.consoleArea.setEditable(false);
		this.consoleArea.setMargin(new Insets(1, 10, 5, 2));
		this.consoleInput.addActionListener(this);
		this.add(consoleInput, BorderLayout.SOUTH);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		String input = consoleInput.getText();
		String answer = inputHandler.onInput(input);
		consoleInput.selectAll();
		consoleArea.append(answer + newline);
		consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
	}

	@Override
	public void setInputHandler(InputHandler handler) {
		this.inputHandler = handler;
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}
}
