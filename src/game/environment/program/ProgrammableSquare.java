package game.environment.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Hero;
import game.environment.Creatable;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.onstep.NullOnStepAction;
import game.environment.onstep.OnStepAction;
import game.environment.update.NullUpdateAction;
import game.environment.update.UpdatableSquare;
import game.environment.update.UpdateAction;
import gui.graphics.GraphicView;
import main.Hub;

public class ProgrammableSquare extends UpdatableSquare implements Creatable{

	private ProgramState baseState;
	private java.util.Map<Integer,Square> knownSquares = new HashMap<Integer,Square>();
	private List<Object> data = new ArrayList<Object>();;

	private ProgramState currentState;
	private OnStepAction currentBlackAction;
	private OnStepAction currentWhiteAction;
	private UpdateAction currentUpdateAction;
	private double secondsSinceLastFrame;
	
	public ProgrammableSquare(int shapeType, int blackColour, int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super(5,shapeType, blackColour, whiteColour, ints, floats);
		this.actionType = 7;		
	}
	
	protected void loadActions(Iterator<Integer> ints, Iterator<Float> floats){
		super.loadActions(ints, floats);
		final ProgrammableSquare myself = this;
		this.blackAction = new NullOnStepAction(){
			@Override
			public void act(Hero subject){
				if(currentBlackAction!=null){
					currentBlackAction.act(subject);
				}
				currentState.on("stepBlack",myself);
			}
			@Override
			public void setTarget(Object target){
				if(currentBlackAction!=null){
					currentBlackAction.setTarget(target);
				}
			}
			@Override
			public int targetType(){
				if(currentBlackAction!=null){
					return currentBlackAction.targetType();
				}
				else {
					return -1;
				}
			}
		};
		this.whiteAction = new NullOnStepAction(){
			@Override
			public void act(Hero subject){
				if(currentWhiteAction!=null){
					currentWhiteAction.act(subject);
				}
				currentState.on("stepWhite",myself);
			}
			@Override
			public void setTarget(Object target){
				if(currentWhiteAction!=null){
					currentWhiteAction.setTarget(target);				
				}
			}
			@Override
			public int targetType(){
				if(currentWhiteAction!=null){
					return currentWhiteAction.targetType();
				}
				else {
					return -1;
				}
			}
		};
		this.updateAction = new NullUpdateAction(){
			@Override
			public void act(Double seconds){
				secondsSinceLastFrame = seconds;
				if(currentUpdateAction!=null){
					currentUpdateAction.act(seconds);
					if(currentUpdateAction.hasCrestedLimit()){
						currentState.on("limitReached", myself);						
					} else {
						currentState.on("update", myself);
					}
				}
				else {
					currentState.on("update", myself);
				}
			}
		};
		this.updateAction.setTarget(this);
		this.baseState = new ProgramState();
		this.baseState.setTarget(this);
		this.baseState.loadFrom(ints, floats);
		this.currentState = baseState;
	}

	public void setData(Integer dataIndex, Object datum){
		data.set(dataIndex, datum);
	}
	public void addData(Object datum){
		data.add(datum);
	}
	public Object getLastData(){
		return data.get(data.size()-1);
	}
	public Object getData(Integer dataIndex) {
		return data .get(dataIndex);
	}
	
	@Override
	public List<SquareAction> getActions(){
		List<SquareAction> actions = super.getActions();
		actions.add(baseState);
		return actions;
	}
	
	@Override
	public void create(){
		baseState.act(this);
	}

	public Object getSubject(int type, DataHolder holder) {
		switch(type){
		case 0:	return this;
		case 1: return Hub.getHero(true);
		case 2: return Hub.getHero(false);		
		case 3: return secondsSinceLastFrame;
		case 4: return (Integer)holder.getData("subject");
		case 5: return (Float)  holder.getData("subject");
		case 6: return getData((Integer)holder.getData("dataIndex"));
		case 7: {
			//NOTE: This does not display the square by itself, if you want to display this "phantom" square, use DisplaySquareAdvancedAction.
			//		Which has the subject type of Square aka 7.
			int knownIndex = (Integer)holder.getData("knownIndex");
			if(!knownSquares.containsKey(knownIndex)){
				Square square = Hub.map.getTemplateSquares().get((Integer)holder.getData("templateIndex"));
				square = Square.copy(square);
				knownSquares.put(knownIndex, square);
			}
			return knownSquares.get(knownIndex);
		}
		}
		return null;
	}
	

	private static int ksId = 0;
	public Integer getNewKnownSquaresId(){
		return ksId++;
	}
	
	public void setUpdateAction(UpdateAction action){
		action.setTarget(this);
		this.currentUpdateAction = action;
	}
	
	public void setBlackOnStepAction(OnStepAction action){
		this.currentBlackAction = action;
	}
	public void setWhiteOnStepAction(OnStepAction action){
		this.currentWhiteAction = action;
	}

	public void setBaseState(ProgramState state) {
		this.baseState = state;
	}
	public void setState(ProgramState state){
		this.currentState = state;
	}

	public ProgramState getState() {
		return currentState;
	}
	public ProgramState getBaseState() {
		return baseState;
	}

}
