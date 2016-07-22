package duo.messages;

import java.net.Socket;

import game.menu.HostMenu;

public class JoinGameMessage extends Message{
	private static final long serialVersionUID = 7129429698698309846L;
	private String gameName;
	public JoinGameMessage( String gameName){
		this.gameName = gameName;
	}
	
	@Override
	public void act(Socket socket) {
		HostMenu.gameButton.changeText("Start Game");
	}

}
