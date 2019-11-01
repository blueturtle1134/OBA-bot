package oba.bank.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import oba.bank.bot.BankApplication;

public class Controls {
	
	JFrame frame;
	JPanel mainPanel;
	private JTextArea output;
	
	public Controls() {
		frame = new JFrame("OBA Bot Controls");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		frame.setContentPane(mainPanel);
		
		JLabel title = new JLabel("OBA Bot version "+BankApplication.getVersion());
		title.setFont(new Font("Console", Font.BOLD, 30));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setPreferredSize(new Dimension(500,50));
		title.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainPanel.add(title);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				BankApplication.save();
			}
			
		});
		saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainPanel.add(saveButton);
		
		JButton exitButton = new JButton("Save and Stop Bot");
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				BankApplication.save();
				BankApplication.stop();
			}
			
		});
		exitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainPanel.add(exitButton);
		
		output = new JTextArea();
		output.setEditable(false);
		output.setAlignmentX(0.0f);
		output.setText("No logs yet...");
		mainPanel.add(output);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public void print(String s) {
		output.setText("Last log: "+s);
	}
}
