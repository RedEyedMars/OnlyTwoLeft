package game.environment.onstep;

import game.Hero;
import game.environment.update.UpdatableSquare;

public class DeactivateOnStepAction extends OnStepAction<UpdatableSquare>{
	@Override
	public void act(Hero subject) {
		target.deactivate();
	}
	@Override
	public void setTarget(UpdatableSquare target){
		if(this.target==null){
			this.target = target;
		}
	}
	@Override
	public int targetType(){
		return 1;
	}
	@Override
	public int getIndex() {
		return 4;
	}
	@Override
	public OnStepAction<UpdatableSquare> create() {
		return new DeactivateOnStepAction();
	}
}
