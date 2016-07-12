package duo.messages;

import java.net.Socket;
public class AddPlayerMessage extends Message{
	private static final long serialVersionUID = -8715731293278294725L;
	private String playerName;
	public AddPlayerMessage(String name){
		this.playerName = name;
	}

	@Override
	public void act(Socket socket) {
	}

}
