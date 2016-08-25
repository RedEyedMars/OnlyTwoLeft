package game.environment.onstep;

import game.Hero;
import game.environment.Square;
import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;

public class SafeOnStepAction extends OnStepAction<Square>{
	@Override
	public void act(Hero subject) {
		if(target instanceof UpdatableSquare){
			UpdatableSquare square = ((UpdatableSquare)target);
			for(UpdateAction updateAction:square.getAction()){
				if(updateAction.getIndex()==0||updateAction.getIndex()==1){
					if(subject.getXAcceleration()==0){
						subject.setXVelocity(updateAction.getFloat(0)/0.9f);
					}
					if(subject.getYAcceleration()==0){
						if(updateAction.getFloat(1)<0){
							subject.setYVelocity(subject.getYVelocity()+updateAction.getFloat(1)/0.9f);
						}
					}
				}
			}
		}
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
