package duo.messages;

import duo.Handler;

/**
 * While in {@link game.menu.JoinMenu} or {@link game.menu.HostMenu} the Host might want to kick the joining player,
 * or the joining player might want to remove themselves from the lobby.
 * The KickFromGameMessage relays both of these {@link duo.messages.Message}'s to the partnered {@link duo.client.Client}.
 * @author Geoffrey
 *
 */
public class KickFromGameMessage extends Message {
	//For Message sending.
	private static final long serialVersionUID = 387982011745657723L;

	/**
	 * When the joining side receives this {@link duo.messages.Message} the {@link game.menu.JoinMenu} is notified to return it to the state of "looking for a game to join". 
	 */
	@Override
	public void act(Handler handler) {
		//Call the kick method, this will free this client from being tethered to the game they are currently waiting to start.
		handler.getMenu().kick();
	}

}
