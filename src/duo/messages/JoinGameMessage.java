package duo.messages;

import java.net.Socket;

import duo.Handler;
import game.menu.HostMenu;

public class JoinGameMessage extends Message{
	private static final long serialVersionUID = 7129429698698309846L;
	private String playerName;
	private String gameName;
	public JoinGameMessage(String gameName, String playerName){
		this.gameName = gameName;
		this.playerName = playerName;
	}
	
	@Override
	public void act(Handler handler) {
		handler.getMenu().playerJoins(playerName);
	}

}
