package com.richard.chat;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main implements ActionListener, KeyListener{

	//components
	Window frm; //the main window
	private JTextField textfield; //the typing field
	JTextArea textarea; //the chat history area
	private JScrollPane jsp; //so you can scroll
	private JButton connect, send; //buttons for the window
	
	//objects
	Socket socket; //the socket you use
	boolean connected = false; // if you are connected
	private boolean running = true; //if the chat is running
	int CONNECTPORT = 9000; // the port for the client
	int SERVERPORT = 9000; // the port for the server
	
	//in and out streams
	private BufferedReader in;
	private PrintStream out;
	
	
	//if a port is given as an argument, use that port
	Main(int PORT) {
		this();
		this.CONNECTPORT = PORT;
		this.SERVERPORT = PORT;
	}
	
	//entry point for the JVM
	public static void main(String[]Args) {
		new Main();
	}
	
	
	//The constructor, the main algorithm
	Main() {
		
		/*
		 * 	The first part creates the frame
		 * */
		//create a frmae
		frm = new Window("Chat", 500, 375);
		
		//create the JTextArea
		textarea = new JTextArea();
		textarea.setEditable(false);
		
		//create the JScrollPanel
		jsp = new JScrollPane(textarea);
		jsp.setPreferredSize(new Dimension(480, 300));
		frm.add(jsp);
		
		//create the JTextField
		textfield = new JTextField();
		textfield.setPreferredSize(new Dimension(270, 30));
		frm.add(textfield);
		
		//create the send button
		send = new JButton("Send");
		send.setPreferredSize(new Dimension(98, 29));
		send.setActionCommand("send");
		send.setEnabled(false);
		frm.add(send);
		
		//create the connect button 
		connect = new JButton("Connect");
		connect.setPreferredSize(new Dimension(98, 29));
		connect.setActionCommand("connect");
		frm.add(connect);
		
		//add actionListeners to the buttons
		send.addActionListener(this);
		connect.addActionListener(this);
		
		//add a keyListener to the textfield
		textfield.addKeyListener(this);
		
		//repaint the frame
		frm.repaint();
		frm.revalidate();
		
		/*
		 * 	The second part does the networking part 
		 * */		
		
		//initialize the server Thread and start it
		Receiver r = new Receiver(this, Thread.currentThread());
		Thread serverThread = new Thread(r);
		serverThread.setDaemon(true);
		serverThread.setName("server");
		serverThread.start();
		
		/*
		 * 	wait a bit, until the server has been initialized
		 *  this is very important because the server has to look,
		 *  if the used port is already bound
		 *  if it is, it interrupts the main thread, that terminates itself
		 * */
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			return;
		}
		
		//if everything is fine, the frame becomes visible
		frm.setVisible(true);
		
		//waits for a connection from the server or from the client side
		//a client connnection is established through the AWTEvent-Thread
		while (!connected) {
			try {
				Thread.sleep(100); //sleeps 0.1 second whiule not connected
			} catch (InterruptedException e) {
				return; //if interrupted, end the thread
			}
		}
		
		//enable the send button and change the connect button to disconnect
		connect.setText("Disconnect");
		send.setEnabled(true);
		
		
		//setup the streams
		try {
			out = new PrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			this.addMessage("[ERROR] Could not setup the streams!");
			e.printStackTrace();
		}
		
		//prints out a success message
		textarea.setText("[SYSTEM] You are now connected to " + socket.getRemoteSocketAddress());
		
		//the "main"-loop
		while (running) {
			try {
				//try to receive a message (blocks if no message available)
				this.receiveMessage();
			} 
			//if the socket is closed, end 
			catch (SocketException e) {
				this.addMessage("[SYSTEM] The connection was closed!");
				break;
			}
			//if any other form of i/o exception occurs
			catch (IOException e) {
				this.addMessage("[ERROR] Could not receive message!");
				e.printStackTrace();
			}
		}
	}
	
	//this method reads a message and prints it
	public void receiveMessage() throws IOException {
		
		//read from the stream
		String s = in.readLine();
		
		//if the message contains something, print it
		if (s != null && !s.equals("")) { 
			this.addMessage("[HIM] " + s);
		}
	}
	
	//this method sends a message from the text field
	public void sendMessage() {
		
		//get the message to be sent
		String message = textfield.getText();
		
		//if the message contains nothing, return
		if (message.equals("") || message == null) {
			return;
		}
		
		//write the message to the stream
		out.println(message);
		
		//print the message to the screen
		this.addMessage("[YOU]: " + message);
		
		//clear the text field
		textfield.setText("");
	}
	
	//this method makes adding messages to the screen easier
	public void addMessage(String message) {
		
		//gets the text, adds a newline and the message
		textarea.setText(textarea.getText() + "\r\n" + message);
	}
	
	/**
	 * This method starts a seperate 
	 * thread that connects to a certain host
	 * **/
	public void connect() {
		//get the address
		String address = textfield.getText();
		
		//clear the textfield
		textfield.setText("");
		
		//creates a new Connecter-Object in a seperate thread and starts it
		Connecter c = new Connecter(this, address, CONNECTPORT);
		Thread connectThread = new Thread(c);
		connectThread.setName("Conneting to " + address);
		connectThread.start();
	}
	
	/**
	 * this method returns your public ip by opening a new URL 
	 * connection to Amazon Web Services
	 * 
	 * **/
	public String getIP() throws IOException {	
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		whatismyip.openStream()));

		//read the ip
		String ip = in.readLine();
		
		return ip;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		//the send button is pressed, send the message and repaint
		if (connected && e.getActionCommand().equals("send")) {
			sendMessage();
			frm.repaint();
		}
		
		//if the connect button is pressed
		else if (e.getActionCommand().equals("connect")) {
			//if connected, disconnect
			if (connected) {
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			//if not connected, connect
			else {
				this.connect();
			}
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//if enter is pressed int the textfield and you are connected, send the message
		if (textfield.hasFocus() && e.getKeyCode() == KeyEvent.VK_ENTER && connected) {
			this.sendMessage();
		}
		//if enter is pressed int the textfield and you are not connected, connect
		else if (textfield.hasFocus() && e.getKeyCode() == KeyEvent.VK_ENTER && !connected) {
			this.connect();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
