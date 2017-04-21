package com.richard.chat;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

import javax.swing.JLabel;

public class Receiver implements Runnable{

	private Main main;
	private Thread mainThread;
	
	public Receiver(Main main, Thread mainThread) {
		this.main = main;
		this.mainThread = mainThread;
	}
	
	@Override
	public void run() {
		ServerSocket server = null;
		try {
			try {
				server = new ServerSocket(main.SERVERPORT);
				main.textarea.setText("[SYSTEM] Waiting for incoming connections on Port " + server.getLocalPort());
				main.addMessage("[SYSTEM] Your IP: " + main.getIP());
				main.socket = server.accept();
				main.connected = true;
			}
			catch (BindException e) {
				main.frm.setVisible(false);
				main.frm.removeAll();
				Window w = new Window("ERROR", 250, 100);
				w.add(new JLabel("Could not bind to Port " + main.SERVERPORT));
				w.add(new JLabel("Close all instances of this program!"));
				w.setVisible(true);
				w.repaint();
				mainThread.interrupt();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	
}
