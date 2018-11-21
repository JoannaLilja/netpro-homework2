package client.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import client.controller.Controller;
import shared.GameData;
 
public class Client implements Runnable
{
	
	private SocketChannel client;
	private Controller contr;
	private int id;
	
	public Client(int id)
	{
		this.id = id;
	}
 
	public void run()
	{
		
		contr = new Controller(this, id);
 
		InetSocketAddress serverAddr = new InetSocketAddress("localhost", 8080);
		
		try
		{
			client = SocketChannel.open(serverAddr);
			sendRequest(".start");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public void sendRequest(String request) throws IOException
	{
		byte[] message = request.getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		client.write(buffer);
		
		if (!request.equals(".stop"))
			readResponse();
	}
	
	
	private void readResponse() throws IOException
	{
		GameData gameData = null;
		ByteBuffer buffer = ByteBuffer.allocate(256*2);
		client.read(buffer);
		byte[] byteArr = buffer.array();
		
		ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(byteArr));
		
		try { 
			gameData = (GameData) objIn.readObject();	
		} 
		catch (ClassNotFoundException e){ 
			e.printStackTrace(); 
		}
		
		objIn.close();		
		
		contr.updateInfo(gameData);
	}

}