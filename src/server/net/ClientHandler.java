package server.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;

import server.controller.Controller;
import server.model.Game;
import shared.GameData;

public class ClientHandler
{

	Controller contr;
	SocketChannel clientChannel;
	
	public ClientHandler(SocketChannel clientChannel) throws IOException, InterruptedException, ExecutionException 
	{
		this.clientChannel = clientChannel;
		Game game = new Game();
		contr = new Controller(game, this);
		game.setController(contr);
		
	}
	
	public void read() throws IOException, InterruptedException, ExecutionException
	{
				 
		ByteBuffer buffer = ByteBuffer.allocate(256);
		clientChannel.read(buffer);
		String readData = new String(buffer.array()).trim();

		if(!readData.isEmpty())
		{
			if (readData.equals(".stop"))
				clientChannel.close();
			else
				contr.receiveGuess(readData);
		}
				
	}
	
	public void sendResponse(GameData gameData) throws IOException
	{
		
		byte[] byteArr;
		ByteBuffer buffer;
		
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream ();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream );
		
		objectOutputStream.writeObject(gameData); 
		objectOutputStream.flush();
		objectOutputStream.close();
		
		byteArr = byteOutputStream.toByteArray();
		buffer = ByteBuffer.wrap(byteArr);
				
		clientChannel.write(buffer);
		
		buffer.clear();
		
	}
}
