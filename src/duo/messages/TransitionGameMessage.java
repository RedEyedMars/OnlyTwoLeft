package duo.messages;

import duo.Handler;
import game.menu.TransitionMenu;
import gui.Gui;

/**
 * TransitionGameMessages are sent by the {@link duo.client.Client} that has triggered the end game (either with a death or completing the map).
 * Its purpose is to invoke the {@link game.menu.TransitionMenu} and transition the game state from one map to: a new map, the same map, or the main menu.
 * 
 * @see game.menu.TransitionMenu
 * @author Geoffrey
 *
 */
public class TransitionGameMessage extends Message{
	//For Message sending.
	private static final long serialVersionUID = 7096403795396843139L;

	//If true the map has been completed, if false it means someone has died.
	private boolean isWinner;
	//The number of minutes it took to either complete or fail the map.
	private int minutes;
	//The number of seconds it took to either complete or fail the map.
	private int seconds;
	//The name of the map which has just been completed. (Note this is not the path to that map)
	private String finishedMap;
	//The name of the map to which can be transitioned, if the map was failed this will be "Restart".
	private String nextMap;
	//The colour to which a new game will be controlled if the transition takes place, I.e. if true then a new game started by this Client will have the Black hero to control, false and it'll be White. 
	private boolean controlledColour;
	//This variable denotes whether it is the decision of the receiving Client to proceed to the nextMap. (Should always be true for the Host Client and false for the Joining Client).
	private boolean canProceed;
	
	/**
	 * Initializes the TransitionGameMessage with the appropriate variables.
	 * @param isWinner - If true: the finishedMap was won, if false a {@link game.Hero} died, preventing game completion.
	 * @param minutes - Minutes it took to either fail or complete the map.
	 * @param seconds - Seconds it took to either fail or complete the map.
	 * @param finishedMap - Name of the map which has been failed or completed. This is not the path, but the filename without the extension.
	 * @param nextMap - Name of the map which will be transitioned to if the "proceed" option is selected by the host. "Restart" if the map needs to be played again because of a Failure.
	 * @param controlledColour - If true: the {@link game.Game} will start by controlling the Black {@link game.Hero}, if false: the controlled {@link game.Hero} will be White. 
	 * @param canProceed - Set to true if the receiving {@link duo.client.Client} is the Host Client, meaning that it is the one that controls whether the two clients proceeds to the next map or not. If false then no control over proceeding to the next map is given.
	 */
	public TransitionGameMessage(boolean isWinner, int minutes, int seconds, String finishedMap, String nextMap, boolean controlledColour, boolean canProceed){
		this.isWinner = isWinner;
		this.minutes = minutes;
		this.seconds = seconds;
		this.finishedMap = finishedMap;
		this.nextMap = nextMap;
		this.controlledColour = controlledColour;
		this.canProceed = canProceed;
	}
	
	/**
	 * When received a {@link game.menu.TransitionMenu} is created based on the variables.
	 * Importantly if the canProceed variable is set to false, the TranistionMenu will only have the option to wait or return to the MainMenu.
	 * If its set to true, then the Host has the option to proceed or return to the MainMenu (severing the link). 
	 */
	@Override
	public void act(Handler handler) {
		//Create and employ a TransitionMenu based on the variables contained within this Message.
		Gui.setView(
				new TransitionMenu(
						false,
						isWinner,
						minutes,seconds,
						finishedMap,nextMap,
						controlledColour,
						canProceed));
	}

}
