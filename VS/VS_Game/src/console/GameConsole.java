package console;

import game.Console;
import game.InputHandler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	private NameRequestDialog nameRequest;
	private InputHandler inputHandler;

	public GameConsole() {
		super("Space BWL");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Layout stuff
		this.setLayout(new BorderLayout());
		this.consoleArea.setEditable(false);
		this.consoleArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		this.consoleArea.setMargin(new Insets(1, 10, 5, 2));
		this.consoleInput.addActionListener(this);
		this.consoleInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		this.add(consoleInput, BorderLayout.SOUTH);
		this.add(scrollPane, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				consoleInput.requestFocus();
			}
		});
		this.consoleInput.requestFocus();
		
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (inputHandler != null) {
			String input = consoleInput.getText();
			consoleArea.append(" > " + input + newline);
			inputHandler.onInput(input);
			consoleInput.setText("");
			consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
		}
	}

	@Override
	public void setInputHandler(InputHandler handler) {
		this.inputHandler = handler;
	}

	@Override
	public void setVisible(boolean visible) {
		centerize();
		super.setVisible(visible);
	}

	@Override
	public String waitForName() {
		nameRequest = new NameRequestDialog(this);
		nameRequest.setVisible(true);
		return nameRequest.getEnteredName();
	}

	@Override
	public void writeLine(String text) {
		this.consoleArea.append(text + newline);
	}
	
	public void centerize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getSize().width) / 2,
				(screenSize.height - this.getSize().height) / 2);
	}

}
