package duo.messages;

import duo.Handler;

/**
 * The {@link game.menu.HostMenu} must have the power to remove games that have they created for joining.
 * When this {@link duo.messages.Message} is received by the Server the list of games that are available is updated without the Game sent.
 * @author Geoffrey
 *
 */
public class RemoveGameMessage extends Message{
	//For Message sending.
	private static final long serialVersionUID = 134659052980417677L;
	

	/**
	 * This {@link duo.messages.Message} should not be received by the {@link duo.client.Client} as its purpose is solely to remove games from the Server.
	 */
	@Override
	public void act(Handler handler) {
	}

}
