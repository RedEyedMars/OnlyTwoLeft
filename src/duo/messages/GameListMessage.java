package duo.messages;

import java.util.List;

import duo.Handler;

/**
 * Receives game list objects representing the games currently being offered by "Host"s.
 * Joining games is done by {@link game.menu.JoinMenu}.
 * Hosting games is done by {@link game.menu.HostMenu} .
 * @author Geoffrey
 *
 */
public class GameListMessage extends Message{
	//Serial for message sending.
	private static final long serialVersionUID = 1912775814069331L;

	//The name of each available game.
	private List<String> gameNames;
	//The name of the map that the game will load into.
	private List<String> mapNames;
	//The colour the host will assume, the joiner will join as the opposite.
	private List<String> colours;
	
	/**
	 * Tells this {@link duo.client.Client} to update its game list using the variables stored in this {@link duo.messages.Message}.
	 */
	@Override
	public void act(Handler handler) {		
		//System.out.println(gameNames.size());
		//Clear the currently known games, knowing that the variables herein represent the complete list.
		handler.clearGames();
		//Iterate through each of the element contained.
		for(int i=0;i<gameNames.size();++i){
			//Add one row of game info into the list of games.
			handler.addGame(new String[]{gameNames.get(i),mapNames.get(i),colours.get(i)});
		}
	}

}
