package game.environment;

import game.Action;
import game.Hero;

public abstract class SquareAction implements Action<Hero> {
	public static final SquareAction hazard = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.endGame();
		}
	};

	public static final SquareAction impassible = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.backup(self);
		}
	};
	protected Square self;
	public void setSelf(Square self){
		this.self = self;
	}
};