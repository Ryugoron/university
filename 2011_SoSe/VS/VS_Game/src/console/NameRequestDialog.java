package console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class NameRequestDialog extends JDialog {
	private static final long serialVersionUID = 3609083164144510442L;
	private String enteredName = "";
	private JLabel label = new JLabel("Please enter your planetname");
	private JTextField input = new JTextField(20);
	private JButton closeButton = new JButton("Create planet");

	public NameRequestDialog(JFrame owner) {
		super(owner, "Please enter your name", true);
		addComponents();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setSize(200,100);
	}

	private void addComponents() {
		this.setLayout(new BorderLayout());
		this.closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enteredName = input.getText();
				NameRequestDialog.this.setVisible(false);
			}
		});
		this.add(this.label, BorderLayout.NORTH);
		this.add(this.input, BorderLayout.CENTER);
		
		this.input.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					enteredName = input.getText();
					NameRequestDialog.this.setVisible(false);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					enteredName = input.getText();
					NameRequestDialog.this.setVisible(false);
				}
			}
		});
		
		this.add(this.closeButton, BorderLayout.SOUTH);
	}

	public String getEnteredName() {
		return enteredName;
	}
	
	@Override
	public void setVisible(boolean visible) {
		centerize();
		super.setVisible(visible);
	}
	
	public void centerize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getSize().width) / 2,
				(screenSize.height - this.getSize().height) / 2);
	}

}
