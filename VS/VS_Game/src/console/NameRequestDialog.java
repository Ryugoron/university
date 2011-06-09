package console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JButton closeButton = new JButton("save and close");

	public NameRequestDialog(JFrame owner) {
		super(owner, "Please enter your name", true);
		addComponents();
		this.setSize(200,200);
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
		this.add(this.closeButton, BorderLayout.SOUTH);
	}

	String getEnteredName() {
		return enteredName;
	}
}
