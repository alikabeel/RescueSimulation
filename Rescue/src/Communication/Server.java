//The Class basis and Credits of many methods goes to Geeks for Geeks and New Boston Channel on Youtube

package Communication;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import controller.CommandCenter;
import controller.CommandCenterReceive;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.units.Unit;


public class Server extends JFrame implements Runnable{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private String myIp;
	private CommandCenterReceive CCR;
	private CommandCenter CC;
	private ArrayList<ResidentialBuilding> visibleBuildings;
	private ArrayList<Citizen> visibleCitizens;
	private ArrayList<Unit> emergencyUnits;
	private boolean isLog =true;
	private boolean isDisaster=true;
	private boolean isConnected=false;
	private boolean isOnline=true;
	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public Server(CommandCenter CC)
	{
		super("Server");
		try {
			CCR = new CommandCenterReceive();
			CCR.getGUI().setInVisible();
		}
		catch(Exception e) {}
		try {
			userText = new JTextField();
			userText.setEditable(false);
			userText.addActionListener( 
					new ActionListener() {
						public void actionPerformed(ActionEvent event)
						{
							sendMessage(event.getActionCommand());
							userText.setText("");
						}
					}
					);
			add(userText, BorderLayout.NORTH);
			chatWindow = new JTextArea();
			InetAddress inetAddress;
			add(new JScrollPane(chatWindow));
			setSize(300,150);
			setVisible(true);
			try {
				inetAddress = InetAddress.getLocalHost();
				displayMessage("Your ip is " + inetAddress.getHostAddress() + "\n You may send it to other command centers to aid them in their mission.\n");
			} catch (UnknownHostException e) {
				displayMessage("unable to fetch your ip.\n");
			}

			}catch(Exception e) {}
	}

	//set up and run the server
	public void startRunning()
	{
		try
		{	try {
			server = new ServerSocket(6789, 100);}
		catch(BindException b) {
			return;
		}
			while(true)
			{
				try {
					//connect and have conversation
					waitForConnection();
					setupConnection();
					chatHandler();

				}catch(EOFException eofException)
				{
					displayMessage("\n Server ended the connection! ");
				}finally {
					closeConnection();
				}
			}
		}catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
		
	}

	//wait for connection, then display connection information
	private void waitForConnection() throws IOException
	{
		displayMessage("Waiting for someone to connect ... \n");
		connection = server.accept();
		displayMessage("Now connected to "+ connection.getInetAddress().getHostName());
		isConnected=true;
	}

	// get streat to send and receive data
	private void setupConnection() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		displayMessage("\n Streams are now setup! \n");
	}

	//during the chat conversation
	private void chatHandler() throws IOException
	{
		String message = "You are now connected! ";
		sendMessage(message);
		canType(true);
		do
		{
			//have a conversation
			try {
				Object o =  input.readObject();
				if (o instanceof String && !isDisaster && !isLog)
				{
					message = (String) o;
					displayMessage("\n" + message);
				}
				else
				{	if(isLog && o instanceof String) {
					receiveData(o);
					isLog=false;
				} else if(isDisaster && o instanceof String) {
					isDisaster=false;
					receiveData(o);
				}
				else {
					receiveData(o);
				}
				}
			}catch(ClassNotFoundException classNotFoundException)
			{
				displayMessage("\n Unable to read user message");

			}
			if(!isOnline) {
				message="CLIENT - END";
				CCR.getGUI().setInVisible();
			}
		}while(!message.equals("CLIENT - END"));
	}

	//close streams and sockets after you are done chatting
	public void closeConnection()
	{
		displayMessage("\n Closing connection... \n");
		canType(false);
		try 
		{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException)
		{
		}
		try {
			CCR = new CommandCenterReceive();
			CCR.getGUI().setInVisible();
			isDisaster=true;
			isLog=true;
			isConnected=false;
		}
		catch(Exception e) {}
		
	}

	//send a message to client
	public void sendMessage(String message)
	{
		try
		{
			output.writeObject("Server - "+ message);
			output.flush();
			displayMessage("\nServer - " + message);

		}catch(IOException ioException)
		{
			chatWindow.append("\n ERROR: Unable to fetch user message!");
		}
	}

	//updates chatWindow;

	private void displayMessage(final String text)
	{
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run()
					{
						chatWindow.append(text);
					}
				}
				);
	}


	//let the user type stuff into their box
	private void canType(final boolean tof)
	{
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run()
					{
						userText.setEditable(tof);


					}
				}
				);
	}

	public void receiveData(Object o)
	{
		try {
			CCR.getGUI().setVisible();
			CCR.setupHelp(o);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
this.startRunning();
	}
}
