package eu.compassresearch.core.interpreter.cosim;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.compassresearch.core.interpreter.cosim.communication.Utils;
import eu.compassresearch.core.interpreter.debug.messaging.Message;
import eu.compassresearch.core.interpreter.debug.messaging.MessageCommunicator;
import eu.compassresearch.core.interpreter.debug.messaging.MessageContainer;

public class MessageManager
{

	private BufferedReader input;
	private BufferedOutputStream output;

	public MessageManager(Socket socket) throws IOException
	{
		this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.output = new BufferedOutputStream(socket.getOutputStream());
	}

	public void close()
	{
		try
		{
			input.close();
		} catch (IOException e)
		{
		}
		try
		{
			output.close();
		} catch (IOException e)
		{
		}
	}

	public void send(Message message) throws JsonGenerationException,
			JsonMappingException, IOException
	{
		MessageCommunicator.sendMessage(output, message);
		System.out.println("Sent: " + message);
	}

	public Message receive() throws IOException
	{
		MessageContainer container = null;

		while (container == null)
		{
			container = MessageCommunicator.receiveMessage(input);
			Utils.milliPause(10);
		}
		System.out.println("Read: " + container.getMessage());
		return container.getMessage();
	}
}
