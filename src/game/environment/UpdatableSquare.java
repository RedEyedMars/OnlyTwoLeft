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
	public UpdatableSquare(int actionType,int shapeType, int blackColour, int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super(actionType-3,shapeType,blackColour, whiteColour,ints, floats);
		this.actionType=actionType;
		this.updateAction = UpdateAction.getAction(ints.next()).create();		
		this.updateAction.setArgs(ints,floats);
		this.updateAction.setTarget(this);

		int depends = ints.next();
		for(int i=0;i<depends;++i){
			addDependant(Square.create(ints, floats));
		}
	}
	public UpdatableSquare(int shapeType, int blackColour, int whiteColour,float width, float height,int action, float x, float y,int blackAction,int whiteAction) {
		super(shapeType,blackColour, whiteColour,width,height,blackAction,whiteAction);
		this.actionType=3;
		this.updateAction = UpdateAction.getAction(action).create();		
		this.updateAction.addFloats(x,y);
		this.updateAction.setTarget(this);
	}

	public void recycle() {
		updateAction.undo();
	}
	public void move(float x, float y){
		super.move(x, y);
		for(Square square:dependants){
			square.setX(square.getX()-x);
			square.setY(square.getY()-y);
		}
	}
	public void activate(){
		this.activated = true;
	}
	public void deactivate(){
		this.activated = false;
		this.updateAction.onDeactivate();
	}
	public UpdateAction getAction(){
		return updateAction;
	}

	public void update(double secondsSinceLastFrame){
		if(activated){
			this.updateAction.act(secondsSinceLastFrame);
		}
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

	public void run(){
		activated = this.updateAction.getDefaultState();
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
