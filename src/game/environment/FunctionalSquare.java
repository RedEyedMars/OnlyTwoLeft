package game.environment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.Hero;
import gui.graphics.GraphicEntity;

public class FunctionalSquare extends Square{	

	protected SquareAction blackAction;
	protected SquareAction whiteAction;
	public FunctionalSquare(SquareIdentity id, float size, SquareAction bothAction) {
		this(id,0, size,size, bothAction, bothAction);
	}
	public FunctionalSquare(SquareIdentity id, int visibleTo, float size, SquareAction bothAction) {
		this(id,visibleTo, size,size, bothAction, bothAction);
	}
	public FunctionalSquare(SquareIdentity id, float width, float height, SquareAction bothAction) {
		this(id, 0,width, height, bothAction, bothAction);
	}
	public FunctionalSquare(SquareIdentity id, int visibleTo, float width, float height, SquareAction bothAction) {
		this(id, visibleTo, width, height, bothAction, bothAction);
	}
	public FunctionalSquare(SquareIdentity id, float size, SquareAction blackAction,SquareAction whiteAction) {
		this(id, 0, size,size, blackAction, whiteAction);
	}
	public FunctionalSquare(SquareIdentity id, int visibleTo, float size, SquareAction blackAction,SquareAction whiteAction) {
		this(id, visibleTo, size,size, blackAction, whiteAction);
	}
	public FunctionalSquare(SquareIdentity id, int bufferSize, Iterator<Float> floats, SquareAction bothAction) {
		this(id, 0, bufferSize, floats, bothAction, bothAction);
	}
	public FunctionalSquare(SquareIdentity id, int visibleTo, int bufferSize, Iterator<Float> floats, SquareAction bothAction) {
		this(id, visibleTo, bufferSize, floats, bothAction, bothAction);
	}
	public FunctionalSquare(SquareIdentity id, int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction) {
		this(id, 0, bufferSize, floats, blackAction, whiteAction);		
	}
	public FunctionalSquare(SquareIdentity id, int visibleTo, int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction) {
		super(id, visibleTo, bufferSize, floats);
		if(this.visibleToWhite()){
			this.blackAction = blackAction;
			this.blackAction.setSelf(this);
		}
		if(this.visibleToBlack()){
			this.whiteAction = whiteAction;
			this.whiteAction.setSelf(this);
		}
	}
	public FunctionalSquare(SquareIdentity id,float width, float height, SquareAction blackAction, SquareAction whiteAction) {
		this(id,0,width,height,blackAction,whiteAction);
	}
	public FunctionalSquare(SquareIdentity id, int visibleTo, float width, float height, SquareAction blackAction, SquareAction whiteAction) {
		super(id, visibleTo, width, height);
		if(this.visibleToWhite()){
			this.blackAction = blackAction;
			this.blackAction.setSelf(this);
		}
		if(this.visibleToBlack()){
			this.whiteAction = whiteAction;
			this.whiteAction.setSelf(this);
		}
	}

	public Action<Hero> getOnHitAction(Hero p) {
		if(p.getType().equals("black"))return blackAction;
		else if(p.getType().equals("white"))return whiteAction;
		else return null;
	}

	public boolean isFunctional() {
		return true;
	}

}
