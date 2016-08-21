package game.environment.onstep;

import game.Hero;
import game.environment.update.UpdatableSquare;

public class ActivateOnStepAction extends OnStepAction<UpdatableSquare>{
	@Override
	public void act(Hero subject) {
		target.activate();
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
		return 3;
	}
	@Override
	public OnStepAction<UpdatableSquare> create() {
		return new ActivateOnStepAction();
	}
}
