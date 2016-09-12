package game.environment.onstep;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.Hero;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.update.UpdateAction;
import gui.graphics.GraphicEntity;
import main.Hub;

public class OnStepSquare extends Square {	

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
	public OnStepSquare(int actionType,int shapeType, int blackColour,int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super(actionType,shapeType,blackColour,whiteColour,ints, floats);
	}
	@Override
	protected void loadActions(Iterator<Integer> ints, Iterator<Float> floats) {
		super.loadActions(ints,floats);
		if(actionType==1){
			this.blackAction = OnStepAction.getAction(ints.next());
			if(this.blackAction!=null){
				this.blackAction=this.blackAction.create();
				if(this.blackAction.targetType()==2){
					this.blackAction.setTarget(ints.next());
				}
				this.whiteAction = blackAction;
			}
		}
		else if(actionType==2){
			this.blackAction = OnStepAction.getAction(ints.next());
			if(this.blackAction!=null){
				this.blackAction=this.blackAction.create();
				if(this.blackAction.targetType()==2){
					this.blackAction.setTarget(ints.next());
				}
			}
			this.whiteAction = OnStepAction.getAction(ints.next());
			if(this.whiteAction!=null){
				this.whiteAction=this.whiteAction.create();
				if(this.whiteAction.targetType()==2){
					this.whiteAction.setTarget(ints.next());
				}
			}
		}
	}
	public OnStepSquare(int blackColour, int whiteColour, float width, float height, OnStepAction blackAction, OnStepAction whiteAction) {
		this(0,blackColour, whiteColour, width, height,blackAction.getIndex(),whiteAction.getIndex());
	}
	public OnStepSquare(int shapeType, int blackColour, int whiteColour, float width, float height, int blackAction, int whiteAction) {
		super(blackColour, whiteColour, width, height);
		this.actionType = 0;
		this.blackAction = OnStepAction.getAction(blackAction);
		if(this.blackAction!=null){
			this.actionType=1;
			this.blackAction=this.blackAction.create();
			if(this.blackAction.targetType()==2){
				this.blackAction.setTarget(-1);
			}
		}
		if(blackAction==whiteAction){
			this.whiteAction=this.blackAction;
		}
		else if(whiteAction!=-1){
				this.actionType=2;
				this.whiteAction = OnStepAction.getAction(whiteAction);
				this.whiteAction=this.whiteAction.create();
				if(this.whiteAction.targetType()==2){
					this.whiteAction.setTarget(-1);
				}			
		}
		this.setShape(shapeType);
	}


	public OnStepAction getOnHitAction(Hero hero) {
		if(hero.isBlack()&&blackAction!=null){
			return blackAction;
		}
		else if(hero.isWhite()&&whiteAction!=null){
			return whiteAction;
		}
		else return null;
	}

	public boolean isFunctional() {
		return blackAction!=null||whiteAction!=null;
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
		if(blackAction!=null&&blackAction.targetType()==0){
			blackAction.setTarget(this);
		}
		list.add(blackAction);
		if(blackAction!=whiteAction){
			if(whiteAction!=null&&whiteAction.targetType()==0){
				whiteAction.setTarget(this);
			}
			list.add(whiteAction);
		}
		return list;
	}
	
}
