package duo.messages;

import java.net.Socket;

public class PingMessage extends Message{
	private static final long serialVersionUID = 746508151288345062L;

	@Override
	public void act(Socket socket) {
		System.out.println("Client recieved a Ping");
	}

}
