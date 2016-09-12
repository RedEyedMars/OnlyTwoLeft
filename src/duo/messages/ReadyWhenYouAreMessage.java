package duo.messages;

import duo.Handler;

public class ReadyWhenYouAreMessage extends Message {
	private static final long serialVersionUID = -7802797530339627081L;
	public boolean myColour;
	public long seed;
	public ReadyWhenYouAreMessage(Boolean colour, long seed){
		this.myColour = colour;
	}
	
	@Override
	public void act(Handler handler) {
		handler.send(new PassMessage(new StartGameMessage(!myColour,seed)));
		handler.getMenu().startGame(myColour, seed);
	}

}
