package oba.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import oba.bot.Application;

public class Controls {
	
	JFrame frame;
	JPanel mainPanel;
	
	public Controls() {
		frame = new JFrame("OBA Bot Controls");
		frame.setSize(300, 400);
		frame.setVisible(true);
		
		mainPanel = new JPanel();
		frame.setContentPane(mainPanel);
		
		JButton closeButton = new JButton("Save");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Application.save();
			}
			
		});
	}
}
