package duo.messages;

import duo.Handler;

public class EndGameMessage extends Message{
	private static final long serialVersionUID = -4259968371200229807L;

	@Override
	public void act(Handler handler) {
		System.out.println("end game");
		handler.getHero().endGame();
	}

}
