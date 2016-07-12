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
	protected Square target = this;
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
		this(Arrays.asList(colour, 0, bufferSize).iterator(), floats, bothAction, bothAction);
	}
	public FunctionalSquare(Iterator<Integer> ints, Iterator<Float> floats, SquareAction bothAction) {
		this(ints, floats, bothAction, bothAction);
	}
	public FunctionalSquare(int colour,int bufferSize, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction) {
		this(Arrays.asList(colour, 0, bufferSize).iterator(), floats, blackAction, whiteAction);		
	}
	public FunctionalSquare(Iterator<Integer> ints, Iterator<Float> floats, SquareAction blackAction, SquareAction whiteAction) {
		super(ints, floats);
		this.blackAction = blackAction;
		this.whiteAction = whiteAction;
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
	
	public void setTarget(Square target){
		this.target = target;
	}

	public SquareAction getOnHitAction(Hero p) {
		if(p.getType().equals("black")&&blackAction!=null){
			blackAction.setTarget(target);
			return blackAction;
		}
		else if(p.getType().equals("white")&&whiteAction!=null){
			whiteAction.setTarget(target);
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
	public void saveTo(List<Object> toSave) {
		super.saveTo(toSave);
		if(this!=target){
		//	toSave.add(Hub.map.getSquares().indexOf(target));
		}
	}
	@Override
	public void saveActions(List<Object> toSave){
		if(blackAction!=null){
			blackAction.saveTo(toSave);
		}
		if(whiteAction!=null&&whiteAction!=blackAction){
			whiteAction.saveTo(toSave);
		}
	}
	@Override
	public List<Action> getActions() {
		List<Action> list = super.getActions();
		list.add(blackAction);
		if(blackAction!=whiteAction){
			list.add(whiteAction);
		}
		return list;
	}

}
