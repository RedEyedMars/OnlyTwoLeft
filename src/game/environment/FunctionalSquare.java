package game.environment;

import java.util.Iterator;

import game.Action;
import game.Hero;
import gui.graphics.GraphicEntity;

public class FunctionalSquare extends Square{

	protected SquareAction blackAction;
	protected SquareAction whiteAction;
	public FunctionalSquare(byte colour, float size, SquareAction bothAction) {
		this(colour, size,size, bothAction, bothAction);
	}
	public FunctionalSquare(byte colour, float width, float height, SquareAction bothAction) {
		this(colour, width, height, bothAction, bothAction);
	}
	public FunctionalSquare(byte colour, float size, SquareAction blackAction,SquareAction whiteAction) {
		this(colour, size,size, blackAction, whiteAction);
	}
	public FunctionalSquare(byte colour, int bufferSize, Iterator<Float> floats, SquareAction bothAction) {
		this(colour, bufferSize, floats, bothAction, bothAction);
	}
	public FunctionalSquare(byte colour, int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction) {
		super(colour, bufferSize, floats);
		this.blackAction = blackAction;
		this.blackAction.setSelf(this);
		this.whiteAction = whiteAction;
		this.whiteAction.setSelf(this);
	}
	public FunctionalSquare(byte colour, float width, float height, SquareAction blackAction, SquareAction whiteAction) {
		super(colour, width, height);
		this.blackAction = blackAction;
		this.blackAction.setSelf(this);
		this.whiteAction = whiteAction;
		this.whiteAction.setSelf(this);
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
