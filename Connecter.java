package com.richard.chat;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connecter implements Runnable{

	private Main main;
	private String address;
	private int PORT;
	private Socket connection;
	
	public Connecter(Main main, String address, int PORT) {
		this.main = main;
		this.address = address;
		this.PORT = PORT;
	}
	
	@Override
	public void run() {
		main.addMessage("[SYSTEM] Trying to connect to " + address + " on port " + main.CONNECTPORT + "...");
		try {
			
			connection = new Socket(address, PORT);
			if (!main.connected) {
				main.socket = connection;
				main.connected = true;
			}
		} 
		catch (UnknownHostException e) {
			main.addMessage("[ERROR] Could not resolve address: " + address + " on port " + main.CONNECTPORT);
		}
		catch (IOException e) {
			main.addMessage("[ERROR] Could not connect to " + address + " on port " + main.CONNECTPORT);
		}
		
	}

	
}
