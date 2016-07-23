package duo.messages;

import java.net.Socket;

import duo.Handler;

public class PingMessage extends Message{
	private static final long serialVersionUID = 746508151288345062L;

	@Override
	public void act(Handler handler) {
		System.out.println("Client recieved a Ping");
	}

}
