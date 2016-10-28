package com.rem.otl.core.game.environment.program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.rem.otl.core.game.environment.Creatable;
import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.game.environment.SquareAction;
import com.rem.otl.core.game.environment.onstep.NullOnStepAction;
import com.rem.otl.core.game.environment.onstep.OnStepAction;
import com.rem.otl.core.game.environment.update.NullUpdateAction;
import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.environment.update.UpdateAction;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.main.Hub;

public class ProgrammableSquare extends UpdatableSquare implements Creatable{

	private List<ProgramState> stateList;
	private DataHolder variables = new DataHolder(){
		@Override
		public ProgramState getState() {
			return currentState;
		}

		@Override
		public void setState(ProgramState state) {
			currentState = state;
		}

		@Override
		public String[] copiableIntTextureNames() {
			return new String[]{};
		}

		@Override
		public int[] copiableIntTextureRanges() {
			return new int[]{};
		}
	};
	private java.util.Map<Integer,Square> knownSquares = new HashMap<Integer,Square>();
	private List<Object> data = new ArrayList<Object>();

	private ProgramState currentState;
	private OnStepAction currentBlackAction;
	private OnStepAction currentWhiteAction;
	private UpdateAction currentUpdateAction;
	private double secondsSinceLastFrame;
	private List<GraphicEntity> independantImages = new ArrayList<GraphicEntity>();

	public ProgrammableSquare(int shapeType, int blackColour, int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super(5,shapeType, blackColour, whiteColour, ints, floats);
		this.actionType = 7;
	}

	protected void loadActions(Iterator<Integer> ints, Iterator<Float> floats){
		//super.loadActions(ints, floats);
		int numberOfStates = ints.next();
		//ints.next();//blackActionIndex
		//ints.next();//whiteActionIndex
		//ints.next();//updateActionIndex
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
			private double currentUpdateActionStartTime;
			@Override
			public void act(Double seconds){
				secondsSinceLastFrame = seconds;
				timeSinceStart+=seconds;
				if(currentUpdateAction!=null){
					if(currentUpdateAction.hasReachedLimit()){
						secondsSinceLastFrame = timeSinceStart-currentUpdateAction.getTimeToLimit()-currentUpdateActionStartTime;
						currentState.on("limitReached", myself);						
					}
					else {
						currentUpdateAction.act(seconds);
						secondsSinceLastFrame = timeSinceStart-currentUpdateAction.getTimeSinceStart()-currentUpdateActionStartTime;
						currentState.on("update", myself);
					}
				}
				else {
					currentState.on("update", myself);
				}
			}
			@Override
			public float getValue(int index){
				if(currentUpdateAction!=null){
					return currentUpdateAction.getValue(index);
				}
				else {
					return super.getValue(index);
				}
			}
			@Override
			public int getInt(int index){
				if(currentUpdateAction!=null){
					return currentUpdateAction.getInt(index);
				}
				else {
					return super.getInt(index);
				}
			}
			@Override
			public void onActivate(){
				if(currentUpdateAction!=null){
					currentUpdateAction.onActivate();
					currentUpdateActionStartTime = timeSinceStart;
				}
			}
			@Override
			public void onDeactivate(){
				if(currentUpdateAction!=null){
					currentUpdateAction.onDeactivate();
				}
			}
		};
		this.updateAction.setTarget(this);
		//this.updateAction.loadFrom(ints, floats);
		stateList = new ArrayList<ProgramState>();
		for(int i=0;i<numberOfStates;++i){
			stateList.add(new ProgramState());
		}
		for(ProgramState state:stateList){
			state.setTarget(this);
			state.loadFrom(ints, floats);
		}
		this.currentState = stateList.get(0);
	}

	@Override
	public List<SquareAction> getActions(){
		List<SquareAction> actions = new ArrayList<SquareAction>();
		for(ProgramState state:stateList){
			actions.add(state);
		}
		return actions;
	}

	@Override
	protected void saveActions(List<Object> toSave){
		stateList = stateList.get(0).getAllStates();
		toSave.add(stateList.size());
		for(ProgramState state:stateList){
			state.saveTo(toSave);
		}
	}

	@Override
	public void create(){
		stateList.get(0).act(this);
	}

	public Object getSubject(int type, DataHolder holder) {
		switch(type){
		case 0:	return this;
		case 1: return Hub.getHero(true);
		case 2: return Hub.getHero(false);		
		case 3: return secondsSinceLastFrame;
		case 4: return (Integer)holder.getData("subject");
		case 5: return (Float)  holder.getData("subject");
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
		case 8: return updateAction.getTimeSinceStart();
		case 9: return (OnStepAction)holder.getData("subject");
		case 10: return (UpdateAction)holder.getData("subject");
		}
		return null;
	}

	public Variable getVariable(String variableName){
		return variables.getVariable(variableName);
	}

	private static int ksId = 0;
	public Integer getNewKnownSquaresId(){
		return ksId++;
	}

	public void setUpdateAction(UpdateAction action){
		action.setTarget(this);
		this.currentUpdateAction = action;
		if(this.isActive()){
			updateAction.onActivate();
			update(secondsSinceLastFrame);
		}
		else {
			action.onDeactivate();
		}
	}

	public void setBlackOnStepAction(OnStepAction action){
		this.currentBlackAction = action;
	}
	public void setWhiteOnStepAction(OnStepAction action){
		this.currentWhiteAction = action;
	}

	public void setBaseState(ProgramState state) {
		stateList.set(0,state);
	}
	public void setState(ProgramState state){
		state.act(this);
		this.currentState = state;

	}

	public ProgramState getState() {
		return currentState;
	}
	public ProgramState getBaseState() {
		return stateList.get(0);
	}

	public int getStateIndex(ProgramState state){
		return stateList.indexOf(state);
	}

	public ProgramState getStateFromIndex(Integer index) {
		return stateList.get(index);
	}

	public void setVariable(String name, Object value) {
		this.variables.setData(name,value);
	}

	public void addIndependantImage(GraphicEntity image) {
		if(!this.independantImages .contains(image)){
			this.independantImages.add(image);
			addChild(image);
		}
	}
	private float xOffset = 0f;
	private float yOffset = 0f;
	@Override
	public void reposition(float x, float y){
		xOffset = x-getX();
		yOffset = y-getY();
		super.reposition(x, y);
	}
	@Override
	public float offsetX(int index){
		if(this.independantImages.contains(getChild(index))){
			return getChild(index).getX()+xOffset-getX();
		}
		else return super.offsetX(index);
	}
	@Override
	public float offsetY(int index){
		if(this.independantImages.contains(getChild(index))){
			return getChild(index).getY()+xOffset-getY();
		}
		else return super.offsetY(index);
	}
	@Override
	public void resize(float x, float y){
		if(independantImages!=null){
			List<Float> previousWidth = new ArrayList<Float>();
			List<Float> previousHeight = new ArrayList<Float>();
			for(GraphicEntity entity:independantImages){
				previousWidth.add(entity.getWidth());
				previousHeight.add(entity.getHeight());
			}
			super.resize(x, y);
			for(int i=0;i<independantImages.size();++i){
				independantImages.get(i).resize(previousWidth.get(i), previousHeight.get(i));
			}
		}
		else {

			super.resize(x, y);
		}
	}

	public void addOverlayImage(GraphicEntity image) {
		image.reposition(getX(), getY());
		image.resize(getWidth(), getHeight());
		addChild(image);
	}

}
