package com.rem.otl.core.duo.messages;

import com.rem.otl.core.duo.Handler;

/**
 * Used when one client is trying to start the {@link com.rem.otl.core.game.Game} at the same time. i.e. RaceMode which would like to have
 * the two clients as synched as possible. 
 * @author Geoffrey
 *
 */
public class ReadyWhenYouAreMessage extends Message {
	//For Message sending.
	private static final long serialVersionUID = -7802797530339627081L;
	//The colour to start the game with.
	public boolean myColour;
	//The seed which will dictate the random elements in the Game.
	public long seed;
	/**
	 * Initializes the {@link com.rem.otl.core.duo.messages.Message} with the colour and seed for the game. The colour is that of the receiver's {@link com.rem.otl.core.game.hero.Hero}.
	 * @param colour - The colour that the joined client's game will control.
	 * @param seed - The seed for the {@link com.rem.otl.core.game.Game}, this seed dictates the random elements for the map, ensuring that both clients have the same map.
	 */
	public ReadyWhenYouAreMessage(Boolean colour, long seed){
		this.myColour = colour;
		this.seed = seed;
	}
	
	/**
	 * Sends back a {@link com.rem.otl.core.duo.messages.StartGameMessage} completing the loop of other client sending ReadyWhenYouAreMessage to this client, this client sending its reply.
	 * @param handler - the handler holds the menu which will start the game, and sends more message. This handler is the handler which received the {@link com.rem.otl.core.duo.messages.Message}.
	 */
	@Override
	public void act(Handler handler) {
		//Sends a StartGameMessage, this should start the game at about the same time as this client.
		handler.send(new PassMessage(new StartGameMessage(!myColour,seed,System.currentTimeMillis())));
		//Also start this Client's game.
		handler.getMenu().startGame(myColour, seed,0);
	}

}
