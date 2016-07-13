package game.environment;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import game.Action;

public class UpdatableSquare extends FunctionalSquare {
	private UpdateAction updateAction;
	private boolean activated = false;
	public UpdatableSquare(int colour, int bufferSize, Iterator<Float> floats, SquareAction bothAction, UpdateAction updateAction) {
		this(colour, 0, bufferSize,null, floats, bothAction, bothAction, updateAction);
	}
	public UpdatableSquare(int colour, int visibleTo, int bufferSize,Iterator<Integer> ints, Iterator<Float> floats, SquareAction bothAction, UpdateAction updateAction) {
		this(colour, visibleTo, bufferSize,ints, floats, bothAction, bothAction, updateAction);
	}
	public UpdatableSquare(int colour, int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction, UpdateAction updateAction) {
		this(colour, 0, bufferSize,null, floats, blackAction, whiteAction, updateAction);		
	}
	public UpdatableSquare(int colour, int visibleTo, int bufferSize, Iterator<Integer> ints, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction, UpdateAction updateAction) {
		super(colour, visibleTo, bufferSize,ints, floats, blackAction, whiteAction);
		if(blackAction==null&&whiteAction==null){
			actionType=3;
		}
		else {
			actionType+=3;//it's set to 1 or 2 by FunctionalSquare, so +=3 means 4 or 5
		}
		try {
			this.updateAction = updateAction.getClass().newInstance();
			this.updateAction.setFloats(floats);
			this.updateAction.setSelf(this);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public UpdateAction getAction(){
		return updateAction;
	}
	
	public void update(double secondsSinceLastFrame){
		if(activated){
			this.updateAction.act(secondsSinceLastFrame);
		}
	}
	public void activate(){
		this.activated = true;
	}
	public void deactivate(){
		this.activated = false;
	}

	@Override
	public List<Action> getActions() {
		List<Action> list = super.getActions();
		list.add(updateAction);
		return list;
	}
}
