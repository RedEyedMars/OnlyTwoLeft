package game.environment.onstep;

import game.Hero;
import game.environment.Square;
import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;

public class WallOnStepAction extends OnStepAction<Square>{
	@Override
	public void act(Hero subject) {
		
	}
	public boolean isPassible(){
		return false;
	}
	@Override
	public int getIndex() {
		return 1;
	}
	@Override
	public OnStepAction<Square> create() {
		return this;
	}
}
