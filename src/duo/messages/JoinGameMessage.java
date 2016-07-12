package duo.messages;

import java.net.Socket;

public class JoinGameMessage extends Message{
	private static final long serialVersionUID = 7129429698698309846L;
	private String gameName;
	public JoinGameMessage( String gameName){
		this.gameName = gameName;
	}
	
	@Override
	public void act(Socket socket) {		
	}

}
