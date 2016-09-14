package game.hero;

import duo.messages.MoveHeroMessage;
import game.Game;

public class ConnectedHero extends Hero{

	private boolean control;
	public ConnectedHero(boolean control, Game game, boolean colour) {
		super(game, colour);
		this.control = control;
	}
	public ConnectedHero(boolean control, String texture, Game game, boolean colour) {
		super(texture, game, colour);
		this.control = control;
	}
	@Override
	public void move(float x, float y){
		if(control){
			super.move(x,y);
			MoveHeroMessage.send(x,y);
		}
	}
}
