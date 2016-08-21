package duo.messages;

import duo.Handler;
import duo.client.Client;

/**
 * Similar to the purpose of the {@link duo.messages.SendMapMessage} the LoadMapMessage is sent by the "Joined" {@link duo.client.Client}.
 * This is needed because the Joined client is not required to have the map data, only the host needs to have maps.
 * However if the Joined client recognizes that she has stepped on a square which requires the changing of {@link game.environment.Map}s then a new map has to be sent to the joined {@link duo.client.Client}.
 * This message serves to send the "next map" or "restart the map" by sending the map data from the Host client to the Joined client.
 * @see duo.messages.SendMapMessage
 * @author Geoffrey
 *
 */
public class LoadMapMessage extends Message{
	//For Message sending.
	private static final long serialVersionUID = 4261279375161664330L;

	//The name of the file to be sent. If "Restart", then the current Map is resent.
	private String filename;
	//After the message is sent, the second message is acted upon, this is to avoid any discontinuity between sending the map and having the transition menu.
	private Message onEnd;
	/**
	 * Initializes the two variables to be sent with this {@link duo.messages.Message}: The filename of the map, and the {@link duo.messages.Message} to be acted upon after the map has been sent.
	 * @param filename - The {@link game.environment.Map}'s filename. If it is "Restart" then the current map's data will be refreshed. 
	 * @param onEnd - To avoid any discontinuity, there is an associated "onEnd" {@link duo.messages.Message} which is acted upon after this {@link duo.messages.Message} has been received.
	 */
	public LoadMapMessage(String filename, Message onEnd){
		//Set this Message's filename.
		this.filename = filename;
		//Set this Message's onEnd message.
		this.onEnd = onEnd;
	}
	
	/**
	 * Sends the new map's data using the {@link duo.messages.SendMapMessage}'s send method, as well as calls the onEnd {@link duo.messages.Message}'s act function.
	 * @param handler - The handler of this {@link duo.client.Client}, used as a starting point for calling functions deeper in the game. 
	 */
	@Override
	public void act(Handler handler) {
		//Send the map data in the file back to the Joined Client. Note that no further action is needed besides the loading of the map so a BlankMessage is sent as the onEnd.
		Client.sendMapMessage(filename, new BlankMessage());
		//The onEnd Message is acted upon, usually this should be the TransitionGameMessage, but has been left for general use. 
		onEnd.act(handler);
	}

}
