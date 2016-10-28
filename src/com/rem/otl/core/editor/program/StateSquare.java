package com.rem.otl.core.editor.program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rem.otl.core.editor.Button;
import com.rem.otl.core.editor.ButtonAction;
import com.rem.otl.core.game.environment.program.ProgramState;
import com.rem.otl.core.game.environment.program.action.ProgramAction;
import com.rem.otl.core.game.environment.program.condition.FreeProgramCondition;
import com.rem.otl.core.game.environment.program.condition.ProgramCondition;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.HoverEvent;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.gui.inputs.MouseListener;
import com.rem.otl.core.main.Hub;

public class StateSquare extends GraphicEntity implements Iterable<StateSquare>{
	private StateSquare parentStateSquare;

	private ProgramState state;

	private List<StateSquare> subStates = new ArrayList<StateSquare>();
	private List<StateSquare> superStates = new ArrayList<StateSquare>();
	private List<ConditionArrow> subConditions = new ArrayList<ConditionArrow>();
	private List<ActionEditor> actions = new ArrayList<ActionEditor>();

	private ProgramSquareEditor editor;

	private Button linkNewStateButton;

	private static final float EMPTY_WIDTH = 0.125f;
	private static final float EMPTY_HEIGHT = 0.075f;

	private static int sid=0;
	private int id = sid++;

	public StateSquare(final ProgramSquareEditor editor,ProgramState programState, StateSquare parentSquare) {
		super("squares",Hub.BOT_LAYER);
		this.parentStateSquare = parentSquare;
		this.editor=editor;
		this.state = programState;
		this.setFrame(2);
		
		if(parentSquare!=null){
			reposition(parentSquare.getX()+parentSquare.getWidth()+0.15f,parentSquare.getY());
		}
		else {
			reposition(0.05f,0.5f);
		}
		for(ProgramAction action:state.getActions()){
			ActionEditor ae = editor.createActionEditor(action);
			ae.reposition(getX()+0.01f,getY()+0.01f);
			addActionEditor(ae);
		}

		final StateSquare self = this;
		this.linkNewStateButton = new Button("editor_shape_icons",1,"Create a new State and link it to this state",new ButtonAction(){

			private MouseListener newLinkListener = new MouseListener(){
				@Override
				public boolean onClick(ClickEvent event) {
					if(event.getAction()==ClickEvent.ACTION_UP){
						isListening = false;
						Hub.handler.removeOnClick(newLinkListener);
						boolean makeNew = true;
						StateSquare subState = editor.getStateSquare(event.getX(),event.getY());

						if(subState==null||subState==self){
							subState = new StateSquare(editor,state.addSubState("update"),self);
							editor.addChild(subState);
						}
						else {
							makeNew = false;
							subState.setFrame(2);
							state.addState("update",subState.state, new FreeProgramCondition());
						}
						addSubState(subState);

						if(makeNew){
							if(subStates.size()>1){
								subState.reposition(getX()+getWidth()+0.15f, subStates.get(subStates.size()-1-1).getY()+subStates.get(subStates.size()-1-1).getHeight()+0.05f);
							}
							else {
								subState.reposition(getX()+getWidth()+0.15f, getY());
							}
						}
						self.resize(0f, 0f);
						self.reposition(self.getX(),self.getY());
					}

					return true;
				}

				@Override
				public boolean onHover(HoverEvent event) {
					return false;
				}

				@Override
				public void onMouseScroll(int distance) {					
				}
			};
			private boolean isListening = false;
			@Override
			public void act(ClickEvent subject) {
				if(!isListening){
					isListening = true;
					Hub.handler.giveOnClick(newLinkListener);
				}
			}

		},null){
		};
		addChild(linkNewStateButton);		
		editor.addButton(linkNewStateButton);
	}
	public void setupSubStates(){
		List<StateSquare> mySubSquares = new ArrayList<StateSquare>();
		for(String subEvent:ProgramState.eventNames){
			if(state.getSubStates(subEvent)!=null){
				for(int i=0;i<state.getSubStates(subEvent).size();++i){
					boolean makeNew = true;
					StateSquare subState = editor.getStateSquare(state.getSubStates(subEvent).get(i));
					if(subState==null){
						subState = new StateSquare(editor,state.getSubStates(subEvent).get(i),this);
						editor.addChild(subState);
					}
					else {
						makeNew = false;
					}
					addSubState(subState);
					if(makeNew){
						if(subStates.size()>1){
							subState.reposition(getX()+getWidth()+0.15f, subStates.get(subStates.size()-1-1).getY()+subStates.get(subStates.size()-1-1).getHeight()+0.05f);
						}
						else {
							subState.reposition(getX()+getWidth()+0.15f, getY());
						}
						mySubSquares.add(subState);
					}
				}
			}
		}
		for(StateSquare newState:mySubSquares){
			newState.setupSubStates();
		}
	}
	private void addSubState(StateSquare ss){

		subStates.add(ss);
		ss.superStates.add(this);

		ProgramCondition programCondition = state.getSubConditionTo(ss.state);
		subConditions.add(new ConditionArrow(editor,programCondition,state.getEventTo(ss.state)));
		addChild(subConditions.get(subConditions.size()-1));
	}
	public ProgramState solidify() {/*
		ProgramState state = new ProgramState();
		*/
		state.clearEnterActions();
		for(ActionEditor action:actions){
			state.addAction(action.getAction());
		}
		List<ProgramState> currentSubStates = state.getSubStates();
		List<String> currentEvents = state.getEvents();
		for(int i=0;i<subStates.size();++i){
			if(subStates.get(i).state == currentSubStates.get(i)){
				
				if(currentEvents.get(i)!=subConditions.get(i).getEvent()){
					state.changeSubStateEvent(currentEvents.get(i),currentSubStates.get(i),subConditions.get(i).getEvent());
				}
			}
		}
		return state;
	}
	public void moveView(float x, float y) {
		reposition(getX()+x,
				getY()+y);
		for(StateSquare state:subStates){
			if(state.parentStateSquare == this){
				state.moveView(x, y);
			}
		}
	}
	@Override
	public void reposition(float x, float y){
		resize(0f,0f);
		super.reposition(x, y);
	}
	@Override
	public float offsetX(int index){
		if(getChild(index) instanceof ActionEditor){
			return 0.065f;
		}
		else if(getChild(index) instanceof ConditionArrow){
			ConditionArrow arrow = (ConditionArrow)getChild(index);
			return getWidth();
		}
		else if(getChild(index) == linkNewStateButton){
			return getWidth()-linkNewStateButton.getWidth()/2f;
		}
		else return 0f;
	}
	@Override
	public float offsetY(int index){
		if(getChild(index) instanceof ActionEditor){
			int ai = actions.indexOf(getChild(index));
			return getHeight()-0.015f-ai*0.026f-getChild(index).getHeight();
		}
		else if(getChild(index) instanceof ConditionArrow){
			ConditionArrow arrow = (ConditionArrow)getChild(index);
			return getHeight()/2f;
		}
		else if(getChild(index) == linkNewStateButton){
			return getHeight()/2f-linkNewStateButton.getHeight()/2f;
		}
		else return 0f;
	}
	@Override
	public void resize(float x, float y){
		super.resize(EMPTY_WIDTH, EMPTY_HEIGHT+0.026f*actions.size());
		for(ConditionArrow condition:subConditions){
			StateSquare square = subStates.get(subConditions.indexOf(condition));
			condition.resize(square.getX()-(getX()+getWidth()), 
					(square.getY()+square.getHeight()/2f)-(getY()+getHeight()/2f)-0.025f*square.state.getSuperStates().indexOf(state));
		}
		if(linkNewStateButton!=null){
			linkNewStateButton.resize(0.025f,0.025f);
		}
		for(ActionEditor action:actions){
			action.resize(0.025f,0.025f);
		}
	}
	public StateSquare addActionEditor(ActionEditor actionEditor) {
		if(isWithin(actionEditor.getX(), actionEditor.getY())){	
			ActionEditor action = editor.createActionEditor(actionEditor.action);
			addChild(action);
			actions.add(action);
			editor.addButton(action);
			reposition(getX(),
					getY()-0.026f);
			return this;
		}
		for(StateSquare state:subStates){
			state.addActionEditor(actionEditor);
		}
		return null;
	}
	@Override
	public Iterator<StateSquare> iterator() {
		final StateSquare self = this;
		return new Iterator<StateSquare>(){
			private StateSquare currentNode = null;
			private Iterator<StateSquare> nodeIterator = null;
			private Iterator<StateSquare> subIterator = subStates.iterator();
			private boolean sentSelf = false;
			@Override
			public boolean hasNext() {
				return !sentSelf;
			}

			@Override
			public StateSquare next() {
				if(nodeIterator!=null&&nodeIterator.hasNext()){
					return nodeIterator.next();
				}/*
				else if(nodeIterator!=null&&currentNode!=null){
					StateSquare toReturn = currentNode;
					currentNode=null;
					nodeIterator=null;
					return toReturn;s
				}*/
				while(subIterator.hasNext()){
					currentNode = subIterator.next();
					if(currentNode.parentStateSquare == self){
						nodeIterator = currentNode.iterator();
						return next();
					}
				}
				sentSelf = true;
				return self;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}};
	}
	public int getId() {
		return id;
	}
	public ProgramState getState() {
		return state;
	}
	public int numberOfActions() {
		return actions.size();
	}
	public void removeAction(int i){
		this.removeChild(actions.remove(i));
	}
	public ActionEditor getAction(int i){
		return actions.get(i);
	}
	public void removeSelf() {
		for(StateSquare superState:superStates){
			superState.removeSubState(this);
		}
		for(int i=0;i<subStates.size();++i){
			subStates.get(i).removeSuperState(this);
		}
		
		editor.removeChild(this);
	}
	private void removeSuperState(StateSquare toRemove) {
		superStates.remove(toRemove);
		if(parentStateSquare == toRemove){
			if(superStates.isEmpty()){
				removeSelf();
			}
			else {
				parentStateSquare = superStates.get(0);
			}
		}
	}
	private void removeSubState(StateSquare toRemove) {
		int indexOf = subStates.indexOf(toRemove);
		if(indexOf!=-1){
			state.purgeSubState(toRemove.state);
			subStates.remove(indexOf);
			removeChild(subConditions.remove(indexOf));
		}
	}
}
