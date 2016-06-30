package game.environment;

import game.Action;
import game.Hero;

public abstract class SquareAction implements Action<Hero> {
	protected Square self;
	public void setSelf(Square self){
		this.self = self;
	}
};