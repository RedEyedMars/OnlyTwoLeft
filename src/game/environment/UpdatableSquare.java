package game.environment;

import java.util.Iterator;

import game.Action;

public class UpdatableSquare extends FunctionalSquare {
	private UpdateAction updateAction;
	private boolean activated = false;
	public UpdatableSquare(SquareIdentity id, int bufferSize, Iterator<Float> floats, SquareAction bothAction, UpdateAction updateAction) {
		this(id, 0, bufferSize, floats, bothAction, bothAction, updateAction);
	}
	public UpdatableSquare(SquareIdentity id, int visibleTo, int bufferSize, Iterator<Float> floats, SquareAction bothAction, UpdateAction updateAction) {
		this(id, visibleTo, bufferSize, floats, bothAction, bothAction, updateAction);
	}
	public UpdatableSquare(SquareIdentity id, int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction, UpdateAction updateAction) {
		this(id, 0, bufferSize, floats, blackAction, whiteAction, updateAction);		
	}
	public UpdatableSquare(SquareIdentity id, int visibleTo, int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction, UpdateAction updateAction) {
		super(id, visibleTo, bufferSize, floats, blackAction, whiteAction);
		try {
			this.updateAction = updateAction.getClass().newInstance();
			this.updateAction.setFloats(floats);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
}
