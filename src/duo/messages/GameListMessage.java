package duo.messages;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import duo.Handler;
import duo.client.Client;

public class GameListMessage extends Message{
	private static final long serialVersionUID = 1912775814069331L;

	private List<String> gameNames;
	private List<String> mapNames;
	private List<String> colours;
	
	public GameListMessage(){
		gameNames = new ArrayList<String>();
		mapNames = new ArrayList<String>();
		colours = new ArrayList<String>();
	}
	
	@Override
	public void act(Handler handler) {
		//System.out.println(gameNames.size());
		handler.clearGames();
		for(int i=0;i<gameNames.size();++i){
			handler.addGame(new String[]{gameNames.get(i),mapNames.get(i),colours.get(i)});
		}
	}

}
