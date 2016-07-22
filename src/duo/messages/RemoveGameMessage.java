package duo.messages;

import java.net.Socket;

public class RemoveGameMessage extends Message{
	private static final long serialVersionUID = 134659052980417677L;
	private String gameName;
	
	public RemoveGameMessage(String gameName){
		this.gameName = gameName;
	}
	
	@Override
	public void act(Socket socket) {
	}

}
