package console.planet;


import game.networking.GameMessage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import console.Console;
import console.InputHandler;
import console.NameRequestDialog;

public class PlanetConsole extends JFrame implements Console, ActionListener {
	
	private static final long serialVersionUID = 2404403907190763384L;
	private final static String newline = "\n";

	private JTextField consoleInput = new JTextField();
	private NameRequestDialog nameRequest;
	private InputHandler inputHandler;
	
	private Map<Integer,JTextArea> fdSet = new HashMap<Integer,JTextArea>();

	//------- Rückgängig mach Funktion ( ^^ ) -------------
	private final int MAX_COMMANDS = 20;
	private List<String> lastCommands = new ArrayList<String>();
	private int actCommand = 0;
	
	public PlanetConsole() {
		super("Space BWL");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Initstuff
		JTextArea consoleArea = new JTextArea(5, 20);
		fdSet.put(StdFd.StdOut.get(),consoleArea);
		JScrollPane consoleScrollPane = new JScrollPane(consoleArea);
		
		//Right Bar
		JPanel bar = new JPanel();
		bar.setLayout(new GridLayout(2,1));
		
		
		JTextArea planetsArea = new JTextArea(3, 15);
		fdSet.put(StdFd.Planets.get(), planetsArea);
		JScrollPane planetsScrollPane = new JScrollPane(planetsArea);
		
		JTextArea messageArea = new JTextArea(3,5);
		fdSet.put(StdFd.Messages.get(), messageArea);
		JScrollPane messageScrollPane = new JScrollPane(messageArea);
		
		bar.add(planetsScrollPane);
		bar.add(messageScrollPane);
		
		// Layout stuff
		this.setLayout(new BorderLayout());
		
		consoleArea.setEditable(false);
		
		planetsArea.setEditable(false);
//		planetsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
		planetsArea.setMargin(new Insets(2,10,5,4));
		
		consoleArea.setEditable(false);
		consoleArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		consoleArea.setMargin(new Insets(1, 10, 5, 2));
		
		this.consoleInput.addActionListener(this);
		
		this.consoleInput.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP && actCommand > 0){
					--actCommand;
					consoleInput.setText(lastCommands.get(actCommand));
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN && actCommand < lastCommands.size()-1){
					++actCommand;
					consoleInput.setText(lastCommands.get(actCommand));
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN && actCommand == lastCommands.size()-1){
					consoleInput.setText("");
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
				this.keyTyped(e);
			}
		});
		
		this.consoleInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		this.add(consoleInput, BorderLayout.SOUTH);
		this.add(consoleScrollPane, BorderLayout.CENTER);
		this.add(bar, BorderLayout.EAST);
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
			
			this.lastCommands.add(input);
			if(lastCommands.size()>MAX_COMMANDS) lastCommands.remove(0);
			actCommand = lastCommands.size();
			
			inputHandler.onInput(input);
			consoleInput.setText("");
			
			for(JTextArea area : fdSet.values()){
				area.setCaretPosition(area.getDocument().getLength());
			}
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
		fdSet.get(fd).append(GameMessage.revertProtokoll(text) + newline);
	}
	
	@Override
	public void println(StdFd fd, String text) throws IllegalArgumentException {
		this.println(fd.get(), text);
	}

	@Override
	public void println(String text) {
		fdSet.get(StdFd.StdOut.get()).append(GameMessage.revertProtokoll(text) + newline);
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
