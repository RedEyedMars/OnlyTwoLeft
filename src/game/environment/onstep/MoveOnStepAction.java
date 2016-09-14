package game.environment.onstep;

import game.environment.update.UpdatableSquare;
import game.hero.Hero;

public class MoveOnStepAction extends OnStepAction<OnStepSquare>{
	@Override
	public boolean resolve(Hero subject){
		return subject.push(target);
	}
	@Override
	public void act(Hero subject) {
	}
	@Override
	public boolean isPassible(){
		return false;
	}
	@Override
	public int getIndex() {
		return 5;
	}
	@Override
	public OnStepAction<OnStepSquare> create() {
		return this;
	}
}
