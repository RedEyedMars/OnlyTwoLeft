package com.rem.otl.core.game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.environment.update.UpdateAction;
import com.rem.otl.core.gui.graphics.GraphicEntity;

public class AdjustLimiterStartPercentOnCreateAction extends OnCreateAction {

	private float dlsp;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		dlsp=floats.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		if(square.getLast() instanceof UpdatableSquare){
			UpdatableSquare toAdjust = (UpdatableSquare) square.getLast();
			for(UpdateAction updateAction:toAdjust.getAction()){
				updateAction.setValue(UpdateAction.START_PERCENT,
									updateAction.getValue(UpdateAction.START_PERCENT)+dlsp);
			}
		}
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(dlsp);
	}
	@Override
	public int numberOfFloats(){
		return 1;
	}
	@Override
	public int getIndex() {
		return 13;
	}
	public OnCreateAction create(){
		return new AdjustLimiterStartPercentOnCreateAction();
	}
}
