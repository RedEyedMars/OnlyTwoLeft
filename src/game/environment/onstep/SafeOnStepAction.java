package game.environment.onstep;

import game.environment.Square;
import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;
import game.hero.Hero;

public class SafeOnStepAction extends OnStepAction<Square>{
	@Override
	public void act(Hero subject) {
	}		
	@Override
	public int getIndex() {
		return 0;
	}
	@Override
	public OnStepAction<Square> create() {
		return OnStepAction.safe;
	}
}
