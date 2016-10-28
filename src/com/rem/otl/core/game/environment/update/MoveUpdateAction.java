package com.rem.otl.core.game.environment.update;

import java.util.Iterator;

import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.hero.Hero;

public class MoveUpdateAction extends UpdateAction {

	public MoveUpdateAction(){
		defaultState = true;
	}

	@Override
	public void act(Double seconds) {

		if(onLimitReachedAction>-1){
			float moveX = -(limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(x*1),limit));
			float moveY = -(limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(y*1),limit));
			timeSinceStart+=seconds;
			moveX += (limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(x*1),limit));
			moveY += (limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(y*1),limit));
			move(moveX,moveY);
			if(this.hasReachedLimit()){
				timeSinceStart=this.getTimeToLimit();
			}
		}
		else {
			float moveX = -(float)(timeSinceStart*x*1);
			float moveY = -(float)(timeSinceStart*y*1);
			timeSinceStart+=seconds;
			moveX += (float)(timeSinceStart*x*1);
			moveY += (float)(timeSinceStart*y*1);
			move(moveX,moveY);
		}
	}
	protected void move(float dx, float dy) {
		self.move(dx, dy);
		super.move(dx,dy);
	}
	
	
	@Override
	public void onActivate(){
		super.onActivate();
		if(limit!=0){

			timeSinceStart = startAtPercent/limit;

			float moveX = (limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(x*1),limit));
			float moveY = (limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(y*1),limit));
			move(moveX,moveY);

		}
		else {
			timeSinceStart=0;
		}
	}
	@Override
	public UpdateAction create(){
		return new MoveUpdateAction();
	}

	@Override
	public int getIndex() {
		return 1;
	}
}
