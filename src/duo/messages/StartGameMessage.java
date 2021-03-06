package duo.messages;

import duo.Handler;
import main.Main;

/**
 * This {@link duo.messages.Message} tells the {@link duo.client.Client} to start the game on the map currently stored in {@link main.Hub}.map.
 * @author Geoffrey
 *
 */
public class StartGameMessage extends Message {
	//For Message sending.
	private static final long serialVersionUID = -4911635467561953110L;

	//The random seed, this seed is set by the host client and sent to the joining client, this ensures any random elements are handled consistently.
	private long seed;
	//The colour which will start the game. I.e. if the Host is Black(true), then this variable will be White(false), and visa versa.
	private boolean colour;

	/**
	 * Initializes the StartGameMessage with the colour which the partnered {@link game.Game} will be loaded as. Also grabs the random seed.
	 * @param colour - if true, the {@link game.Game} will control the Black coloured {@link game.hero.Hero}, if false, then the controlled {@link game.hero.Hero} will be the White one.
	 */
	public StartGameMessage(boolean colour, Long seed){
		//Get this main's random seed to send to the partnered Client.
		this.seed = seed;
		//Sets the control colour.
		this.colour = colour;
	}
	
	/**
	 * Start the {@link game.Game} with the control colour and the randomizer seed.
	 */
	@Override
	public void act(Handler handler) {
		//Starts the game.
		handler.getMenu().startGame(colour,seed);
	}

}
