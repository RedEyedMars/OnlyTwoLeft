package duo.messages;

import duo.Handler;

/**
 * When the {@link game.Game} ends (usually due to a {@link game.Hero} dying), this {@link duo.messages.Message} is sent.
 * Tells the other Game to also end.
 * @author Geoffrey
 *
 */
public class EndGameMessage extends Message{
	//
	private static final long serialVersionUID = -4259968371200229807L;

	/**
	 * Tells this {@link game.Game} to end.
	 */
	@Override
	public void act(Handler handler) {
		//For debug purposes, tells us that the message to end the game has been received.
		System.out.println("end game");
		//Gets the Hero associated to this handler and tells it to end the game.
		handler.getHero().endGame();
	}

}
