package duo.messages;

import java.net.Socket;

public class AddGameMessage extends Message{
	private static final long serialVersionUID = 1248603591622901924L;

	private String gameName;
	private String mapName;
	private Boolean colour;
	public AddGameMessage(String gameName, String mapName, String colour){
		this.gameName = gameName;
		this.mapName = mapName;
		this.colour = colour.equals("black")?true:colour.equals("white")?false:null;
	}
	
	@Override
	public void act(Socket socket) {		
	}

}
