package duo.messages;

import duo.Handler;

/**
 * JoinGameMessage's are used by a {@link game.menu.JoinMenu} to join a game that has been created by a {@link game.menu.HostMenu}.
 * The {@link duo.messages.Message} triggers a bond between partnered {@link duo.client.Client}s, Server side.
 * @author Geoffrey
 *
 */
public class JoinGameMessage extends Message{
	//For message sending.
	private static final long serialVersionUID = 7129429698698309846L;
	//The player name that is joining the game. This name is superficial and main use is as security for the host client to know who is joining.
	private String playerName;
	//The game name that represents the game the player wants to join.
	private String gameName;
	/**
	 * Initialize the variables this {@link duo.messages.Message} is containing.
	 * @param gameName - String representing the game which the joiner wants to join. 
	 * @param playerName - String telling the host the name of the player joining.
	 */
	public JoinGameMessage(String gameName, String playerName){
		//Store the game's name.
		this.gameName = gameName;
		//Store the player's name.
		this.playerName = playerName;
	}
	
	/**
	 * Tells the {@link game.menu.HostMenu} that a player has joined the lobby, after this the host can start the game.
	 */
	@Override
	public void act(Handler handler) {
		//Relay the message to the menu that a player is trying to join.
		handler.getMenu().playerJoins(playerName);
	}

}
