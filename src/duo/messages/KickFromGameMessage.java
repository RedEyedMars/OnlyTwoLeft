package duo.messages;

import java.net.Socket;

import duo.Handler;
import game.menu.HostMenu;
import game.menu.JoinMenu;
import game.menu.MenuButton;

public class KickFromGameMessage extends Message {
	private static final long serialVersionUID = 387982011745657723L;
	private String gameName;

	public KickFromGameMessage(String gameName) {
		this.gameName=gameName;
	}

	@Override
	public void act(Handler handler) {
		handler.getMenu().kick();
	}

}
