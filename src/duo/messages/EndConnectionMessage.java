package duo.messages;

import java.net.Socket;

import duo.Handler;
import duo.client.Client;

public class EndConnectionMessage extends Message{
	private static final long serialVersionUID = -2064522306521990853L;

	@Override
	public void act(Handler handler) {
		Client.serverEndThisConnection();
	}

}
