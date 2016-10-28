package com.rem.otl.core.game.environment.onstep;

import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.environment.update.UpdateAction;
import com.rem.otl.core.game.hero.Hero;

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
