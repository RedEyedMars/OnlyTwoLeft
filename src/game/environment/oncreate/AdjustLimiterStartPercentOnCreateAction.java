package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;
import gui.graphics.GraphicEntity;

public class AdjustLimiterStartPercentOnCreateAction extends OnCreateAction {

	private float dlsp;
	@Override
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		dlsp=floats.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		if(square.getLast() instanceof UpdatableSquare){
			UpdatableSquare toAdjust = (UpdatableSquare) square.getLast();
			for(UpdateAction updateAction:toAdjust.getAction()){
				updateAction.setLimiterStartPercent(updateAction.getFloat(3)+dlsp);
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
