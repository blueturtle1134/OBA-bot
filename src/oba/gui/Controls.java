package oba.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import oba.bot.Application;

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
		
		JLabel title = new JLabel("OBA Bot Controls");
		title.setFont(new Font("Console", Font.BOLD, 30));
//		title.setAlignmentX(0.0f);
//		title.setHorizontalAlignment(SwingConstants.CENTER);
//		title.setMinimumSize(new Dimension(400,30));
		mainPanel.add(title);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Application.save();
			}
			
		});
//		saveButton.setMinimumSize(null);
//		saveButton.setMaximumSize(null);
//		saveButton.setPreferredSize(null);
//		saveButton.setAlignmentX(0.0f);
		mainPanel.add(saveButton);
		
		output = new JTextArea();
		output.setEditable(false);
//		output.setMinimumSize(new Dimension(400,40));
		mainPanel.add(output);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public void print(String s) {
		output.setText(s);
	}
}
