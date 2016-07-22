package duo.messages;

import java.net.Socket;
import java.util.List;

import duo.client.Client;

public class GameListMessage extends Message{
	private static final long serialVersionUID = 1912775814069331L;

	private List<String> gameNames;
	private List<String> mapNames;
	private List<String> colours;
	
	@Override
	public void act(Socket socket) {
		for(int i=0;i<gameNames.size();++i){
			Client.clearGames();
			Client.addGame(new String[]{gameNames.get(i),mapNames.get(i),colours.get(i)});
		}
	}

}
