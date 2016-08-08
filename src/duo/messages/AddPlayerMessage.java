package duo.messages;

import duo.Handler;

/**
 * Adds a player to the Server.
 * Players in servers can either make games with {@link duo.messages.AddGameMessage}, or join games with {@link duo.messages.JoinGameMessage}.
 * When a server exits, it sends {@link duo.messages.EndConnectionMessage}'s to all of its known players.
 * @author Geoffrey
 *
 */
public class AddPlayerMessage extends Message{
	//Required for message sending.
	private static final long serialVersionUID = -8715731293278294725L;
	//The name of the player to be added. This is used as a reference when a player tries to join a game.
	@SuppressWarnings("unused")
	private String playerName;
	/**
	 * Initializes the {@link duo.messages.Message} before it is send.
	 * @param name - Player name, this non-unique string can be used to different players trying to join a game.
	 */
	public AddPlayerMessage(String name){
		this.playerName  = name;
	}
	
	/**
	 * Empty Method. Only the Server is suppose to receive an AddPlayerMessage
	 */
	@Override
	public void act(Handler handler) {
	}
}
