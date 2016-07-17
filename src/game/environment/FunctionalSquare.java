package game.environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.Hero;
import gui.graphics.GraphicEntity;
import main.Hub;

public class FunctionalSquare extends Square{	

	protected SquareAction blackAction;
	protected SquareAction whiteAction;
	private Square blackTarget=this;
	private Square whiteTarget=this;
	public FunctionalSquare(int colour, float size, SquareAction bothAction) {
		this(colour,0, size,size, bothAction, bothAction);
	}
	public FunctionalSquare(int colour, int visibleTo, float size, SquareAction bothAction) {
		this(colour,visibleTo, size,size, bothAction, bothAction);
	}
	public FunctionalSquare(int colour, float width, float height, SquareAction bothAction) {
		this(colour, 0,width, height, bothAction, bothAction);
	}
	public FunctionalSquare(int colour, int visibleTo, float width, float height, SquareAction bothAction) {
		this(colour, visibleTo, width, height, bothAction, bothAction);
	}
	public FunctionalSquare(int colour, float size, SquareAction blackAction,SquareAction whiteAction) {
		this(colour, 0, size,size, blackAction, whiteAction);
	}
	public FunctionalSquare(int colour, int visibleTo, float size, SquareAction blackAction,SquareAction whiteAction) {
		this(colour, visibleTo, size,size, blackAction, whiteAction);
	}
	public FunctionalSquare(int colour, int bufferSize, Iterator<Float> floats, SquareAction bothAction) {
		this(colour, 0, bufferSize,null, floats, bothAction, bothAction);
	}
	public FunctionalSquare(int colour, int visibleTo,int bufferSize,Iterator<Integer> ints, Iterator<Float> floats, SquareAction bothAction) {
		this(colour, visibleTo, bufferSize,ints, floats, bothAction, bothAction);
	}
	public FunctionalSquare(int colour,int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction) {
		this(colour, 0, bufferSize,null, floats, blackAction, whiteAction);		
	}
	public FunctionalSquare(int colour, int visibleTo, int bufferSize, Iterator<Integer> ints, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction) {
		super(colour,visibleTo,bufferSize,ints, floats);
		this.blackAction = blackAction;
		if(blackAction!=null&&blackAction.numberOfTargets()>0){
			blackTarget = Hub.map.getSquares().get(ints.next());
		}
		this.whiteAction = whiteAction;
		if(whiteAction!=null&&whiteAction.numberOfTargets()>0){
			if(whiteAction!=blackAction){
				whiteTarget = Hub.map.getSquares().get(ints.next());
			}
			else whiteTarget = blackTarget;
		}
		if(whiteAction==blackAction){
			actionType=1;
		}
		else {
			actionType=2;
		}
	}
	public FunctionalSquare(int colour,float width, float height, SquareAction blackAction, SquareAction whiteAction) {
		this(colour,0,width,height,blackAction,whiteAction);
	}
	public FunctionalSquare(int colour, int visibleTo, float width, float height, SquareAction blackAction, SquareAction whiteAction) {
		super(colour, visibleTo, width, height);
		this.blackAction = blackAction;
		this.whiteAction = whiteAction;
		if(whiteAction==blackAction){
			actionType=1;
		}
		else {
			actionType=2;
		}
	}

	public SquareAction getOnHitAction(Hero hero) {
		if(hero.getType().equals("black")&&blackAction!=null){
			blackAction.setTarget(blackTarget);
			return blackAction;
		}
		else if(hero.getType().equals("white")&&whiteAction!=null){
			whiteAction.setTarget(whiteTarget);
			return whiteAction;
		}
		else return null;
	}

	public boolean isFunctional() {
		return true;
	}
	public SquareAction getBlackAction() {
		return blackAction;
	}
	public SquareAction getWhiteAction() {
		return whiteAction;
	}
	@Override
	public List<Action> getActions() {
		List<Action> list = super.getActions();
		if(blackAction!=null){
			blackAction.setTarget(blackTarget);
			list.add(blackAction);
		}
		if(blackAction!=whiteAction){
			whiteAction.setTarget(whiteTarget);
			list.add(whiteAction);
		}
		return list;
	}

}
