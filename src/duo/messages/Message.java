package duo.messages;

import java.io.Serializable;
import java.net.Socket;

public abstract class Message implements Serializable{
	private static final long serialVersionUID = 2753949064349001175L;

	public abstract void act(Socket socket);
}
