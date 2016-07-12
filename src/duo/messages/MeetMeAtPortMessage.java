package duo.messages;

import java.net.Socket;

import duo.client.Client;
import duo.client.Handler;

public class MeetMeAtPortMessage extends Message{
	private static final long serialVersionUID = -5324638348684615251L;

	private int port;
	public MeetMeAtPortMessage(int port){
		this.port = port;
	}
	@Override
	public void act(Socket socket) {
		Client.client.setHandler(new Handler(port));
	}

}
