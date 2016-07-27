package game.environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import game.Action;
import main.Hub;

public class UpdatableSquare extends OnStepSquare {
	private UpdateAction updateAction;
	private boolean activated = false;
	private List<Square> dependants = new ArrayList<Square>();
	public UpdatableSquare(int colour, int bufferSize,Iterator<Integer> ints, Iterator<Float> floats, OnStepAction bothAction, UpdateAction updateAction) {
		this(colour, colour, bufferSize,ints, floats, bothAction, bothAction, updateAction);
	}
	public UpdatableSquare(int blackColour, int whiteColour, int bufferSize,Iterator<Integer> ints, Iterator<Float> floats, OnStepAction bothAction, UpdateAction updateAction) {
		this(blackColour, whiteColour, bufferSize,ints, floats, bothAction, bothAction, updateAction);
	}
	public UpdatableSquare(int colour, int bufferSize, Iterator<Integer> ints, Iterator<Float> floats, OnStepAction blackAction, OnStepAction whiteAction, UpdateAction updateAction) {
		this(colour,colour,bufferSize,ints,floats,blackAction,whiteAction,updateAction);
	}
	public UpdatableSquare(int blackColour, int whiteColour, int bufferSize, Iterator<Integer> ints, Iterator<Float> floats, OnStepAction blackAction, OnStepAction whiteAction, UpdateAction updateAction) {
		super(blackColour, whiteColour, bufferSize,ints, floats, blackAction, whiteAction);
		if(blackAction==null&&whiteAction==null){
			actionType=3;
		}
		else {
			actionType+=3;//it's set to 1 or 2 by FunctionalSquare, so +=3 means 4 or 5
		}
		if(updateAction!=null){
			this.updateAction = updateAction.create();
			this.updateAction.setFloats(floats);
			this.updateAction.setSelf(this);
			int depends = ints.next();
			for(int i=0;i<depends;++i){
				addDependant(Square.create(ints, floats));
			}
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

	public void addDependant(Square square){
		this.dependants.add(square);
		Hub.map.displaySquare(square);
		for(SquareAction action:square.getActions()){
			if(action.numberOfTargets()>0){
				action.setTarget(this);
			}
		}
		addChild(square);
	}
	public List<Square> getDependants() {
		return dependants;
	}
	@Override
	public void displayFor(int colour){
		super.displayFor(colour);
		if(dependants!=null){
			for(Square square:dependants){
				square.displayFor(colour);
			}
		}
	}

	@Override
	public List<SquareAction> getActions() {
		List<SquareAction> list = super.getActions();
		list.add(updateAction);
		return list;
	}
	@Override
	public void saveTo(List<Object> toSave) {
		super.saveTo(toSave);
		toSave.add(dependants.size());
		for(Square square:dependants){
			square.saveTo(toSave);
		}
	}

}
