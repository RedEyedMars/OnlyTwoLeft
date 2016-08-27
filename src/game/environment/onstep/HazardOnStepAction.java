package game.environment.onstep;

import game.Hero;
import game.environment.Square;

public class HazardOnStepAction extends OnStepAction<Square> {
	@Override
	public void act(Hero subject) {
		subject.getGame().loseGame(subject.isBlack());
	}
	public boolean isPassible(){
		return false;
	}
	@Override
	public int getIndex() {
		return 2;
	}
	@Override
	public OnStepAction<Square> create() {
		return this;
	}
}
