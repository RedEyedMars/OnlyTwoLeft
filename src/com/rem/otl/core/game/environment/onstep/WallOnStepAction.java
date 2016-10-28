package com.rem.otl.core.game.environment.onstep;

import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.environment.update.UpdateAction;
import com.rem.otl.core.game.hero.Hero;

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
