package com.richard.chat;

import java.awt.FlowLayout;

import javax.swing.JFrame;

public class Window extends JFrame{

	private static final long serialVersionUID = 2916098252635313488L;

	//this class creates a standard, non-visible, flowLayout, not-resizable window
	public Window(String title, int width, int height) {
		super(title);
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(false);
		this.setLocationRelativeTo(null);
		this.setLayout(new FlowLayout());
		this.setResizable(false);
	}
}
