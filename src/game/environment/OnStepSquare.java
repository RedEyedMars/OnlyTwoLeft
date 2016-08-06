package game.environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.Hero;
import gui.graphics.GraphicEntity;
import main.Hub;

public class OnStepSquare extends Square{	

	protected OnStepAction blackAction;
	protected OnStepAction whiteAction;
	public OnStepSquare(int colour,float size, OnStepAction bothAction) {
		this(colour,colour,size,size, bothAction, bothAction);
	}
	public OnStepSquare(int blackColour, int whiteColour, float width, float height, OnStepAction bothAction) {
		this(blackColour, whiteColour, width, height, bothAction, bothAction);
	}
	public OnStepSquare(int blackColour, int whiteColour, float size, OnStepAction blackAction,OnStepAction whiteAction) {
		this(blackColour, whiteColour, size,size, blackAction, whiteAction);
	}
	public OnStepSquare(int actionType, int blackColour,int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super(blackColour,whiteColour,ints, floats);
		if(actionType==1){
			this.actionType=1;
			this.blackAction = OnStepAction.getAction(ints.next());
			if(this.blackAction!=null){
				this.blackAction.create();
			}
			this.whiteAction = blackAction;
		}
		else if(actionType==2){
			this.actionType=2;
			this.blackAction = OnStepAction.getAction(ints.next());
			if(this.blackAction!=null){
				this.blackAction.create();
			}
			this.whiteAction = OnStepAction.getAction(ints.next()).create();
			if(this.whiteAction!=null){
				this.whiteAction.create();
			}
		}
	}
	public OnStepSquare(int blackColour, int whiteColour, float width, float height, OnStepAction blackAction, OnStepAction whiteAction) {
		super(blackColour, whiteColour, width, height);
		this.blackAction = blackAction.create();
		this.whiteAction = whiteAction.create();
		if(this.whiteAction==this.blackAction){
			actionType=1;
		}
		else {
			actionType=2;
		}
	}

	public OnStepAction getOnHitAction(Hero hero) {
		if(hero.getType().equals("black")&&blackAction!=null){
			if(blackAction.numberOfTargets()==0){
				blackAction.setTarget(this);
			}
			return blackAction;
		}
		else if(hero.getType().equals("white")&&whiteAction!=null){
			if(whiteAction.numberOfTargets()==0){
				whiteAction.setTarget(this);
			}
			return whiteAction;
		}
		else return null;
	}

	public boolean isFunctional() {
		return true;
	}
	public OnStepAction getBlackAction() {
		return blackAction;
	}
	public OnStepAction getWhiteAction() {
		return whiteAction;
	}
	@Override
	public List<SquareAction> getActions() {
		List<SquareAction> list = super.getActions();
		if(blackAction!=null){
			if(blackAction.numberOfTargets()==0){
				blackAction.setTarget(this);
			}
			list.add(blackAction);
		}
		if(blackAction!=whiteAction&&whiteAction!=null){
			if(whiteAction.numberOfTargets()==0){
				whiteAction.setTarget(this);
			}
			list.add(whiteAction);
		}
		return list;
	}

}
