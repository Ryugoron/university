package console;

import game.Console;
import game.InputHandler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GameConsole extends JFrame implements Console, ActionListener {
	
	private static final long serialVersionUID = 2404403907190763384L;
	private final static String newline = "\n";

	private JTextField consoleInput = new JTextField();
	private NameRequestDialog nameRequest;
	private InputHandler inputHandler;
	
	private Map<Integer,JTextArea> fdSet = new HashMap<Integer,JTextArea>();

	public GameConsole() {
		super("Space BWL");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Initstuff
		JTextArea consoleArea = new JTextArea(5, 20);
		fdSet.put(StdFd.StdOut.get(),consoleArea);
		JScrollPane consoleScrollPane = new JScrollPane(consoleArea);
		
		JTextArea planetsArea = new JTextArea(3, 20);
		fdSet.put(StdFd.Planets.get(), planetsArea);
		JScrollPane planetsScrollPane = new JScrollPane(planetsArea);
		
		// Layout stuff
		this.setLayout(new BorderLayout());
		consoleArea.setEditable(false);
		consoleArea.setMargin(new Insets(1, 10, 5, 2));
		planetsArea.setMargin(new Insets(2,10,5,4));
		this.consoleInput.addActionListener(this);
		this.add(consoleInput, BorderLayout.SOUTH);
		this.add(consoleScrollPane, BorderLayout.CENTER);
		this.add(planetsScrollPane, BorderLayout.EAST);
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
			inputHandler.onInput(input);
			consoleInput.setText("");
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

	public void centerize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getSize().width) / 2,
				(screenSize.height - this.getSize().height) / 2);
	}

	@Override
	public void println(int fd, String text) throws IllegalArgumentException{
		if(!fdSet.containsKey(fd)) throw new IllegalArgumentException();
		fdSet.get(fd).append(text + newline);
	}
	
	@Override
	public void println(StdFd fd, String text) throws IllegalArgumentException {
		this.println(fd.get(), text);
	}

	@Override
	public void println(String text) {
		fdSet.get(StdFd.StdOut.get()).append(text + newline);
	}

	@Override
	public boolean testFd(int fd) {
		return fdSet.containsKey(fd);
	}

	@Override
	public boolean testFd(StdFd fd) {
		return fdSet.containsKey(fd.get());
	}

	@Override
	public void clear() {
		fdSet.get(StdFd.StdOut.get()).setText("");
	}

	@Override
	public void clear(int fd) throws IllegalArgumentException {
		fdSet.get(fd).setText("");
	}

	@Override
	public void clear(StdFd fd) throws IllegalArgumentException {
		fdSet.get(fd.get()).setText("");
	}

}
