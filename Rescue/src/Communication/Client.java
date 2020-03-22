//The Class basis and Credits of many methods goes to Geeks for Geeks and New Boston Channel on Youtube
package Communication;

import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame implements Runnable
{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	public CountDownLatch latch;
	private boolean closeSocket=false;
	public boolean notConnected = false;

	public void closeSocket() {
		closeSocket=true;
	}
	public Client(String host, CountDownLatch latch)
	{
		super("Client");
		this.latch = latch;
		serverIP = host;
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
			add(new JScrollPane(chatWindow), BorderLayout.CENTER);
			setSize(300,150);
			setVisible(true);
		}catch(Exception e) {}
	}

	//connect to server
	public void startRunning()
	{
		try {
			connectToServer();
			if (notConnected)
			{
				latch.countDown();
				return;
			}
			setupConnection();
			chatHandler();
		}catch(EOFException eofException)
		{
			displayMessage("\n Client terminated connection");

		}catch(IOException ioException)
		{
			ioException.printStackTrace();
		}finally
		{
			closeConnection();
		}
	}

	//connect to server
	private void connectToServer() throws IOException
	{
		displayMessage("Attempting connection ... \n");
		try 
		{
			connection = new Socket(InetAddress.getByName(serverIP),6789);
		}catch(Exception e)
		{
			notConnected = true;
		}
		if (notConnected)
		{
			displayMessage("Unable to connect to this IP");
			return;
		}
		displayMessage("Connected to: "+ connection.getInetAddress().getHostName());



	}

	//set up streams to send and receive messages
	private void setupConnection() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		displayMessage("\n Your streams are now good to go! \n");
		latch.countDown();
	}

	//while chatting with server
	private void chatHandler() throws IOException
	{
		canType(true);
		do
		{
			try {
				message = (String) input.readObject();
				displayMessage("\n" + message);

			}catch(ClassNotFoundException classNotFoundException)
			{
				displayMessage("\n I dont know that object type");
			}

		}while(!message.equals("Server - END") && !closeSocket);


	}


	//close the streams and sockets
	public void closeConnection()
	{
		if (notConnected)
		{
			return;
		}
		displayMessage("\n closing connection down... ");
		canType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException)
		{
			ioException.printStackTrace();

		}
	}

	//send messages to server

	public void sendMessage(String message)
	{
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			displayMessage("\nCLIENT - " + message);
		}catch(IOException ioException)
		{
			chatWindow.append("\n something messed up sending message");
		}
	}

	public void sendData(Serializable o)
	{
		try {
			if (o == null)
			{
				System.out.println("o was null");
				return;
			}
			if (output == null)
			{
				System.out.println("output stream was null");
				return;
			}
			output.writeObject(o);
			output.flush();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	//change/update chatWindow
	private void displayMessage(final String m)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run() {
						chatWindow.append(m);
					}
				}
				);
	}

	//gives user permission to type crap into the text box
	private void canType(final boolean tof)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run() {
						userText.setEditable(tof);
					}
				}
				);
	}
	public void run() {
		this.startRunning();
	}

}
