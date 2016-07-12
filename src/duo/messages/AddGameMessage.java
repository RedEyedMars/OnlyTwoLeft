package duo.messages;

import java.net.Socket;

public class AddGameMessage extends Message{
	private static final long serialVersionUID = 1248603591622901924L;

	private String gameName;
	private String mapName;
	public AddGameMessage(String gameName, String mapName){
		this.gameName = gameName;
		this.mapName = mapName;
	}
	
	@Override
	public void act(Socket socket) {		
	}

}
