package com.rem.otl.core.game.environment.program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rem.otl.core.game.environment.SquareAction;
import com.rem.otl.core.game.environment.program.action.ProgramAction;
import com.rem.otl.core.game.environment.program.condition.Condition;
import com.rem.otl.core.game.environment.program.condition.FreeProgramCondition;
import com.rem.otl.core.game.environment.program.condition.ProgramCondition;

public class ProgramState implements SquareAction<ProgrammableSquare,ProgrammableSquare> {
	private static final long serialVersionUID = 5712159876488441511L;
	public static final String[] eventNames = new String[]{"stepBlack","stepWhite","update","limitReached"};

	private List<ProgramAction> onEnterActions = new ArrayList<ProgramAction>();
	private LinkedHashMap<String,List<ProgramCondition>> subConditions = new LinkedHashMap<String,List<ProgramCondition>>();
	private LinkedHashMap<String,List<ProgramState>> subStates = new LinkedHashMap<String,List<ProgramState>>();
	private List<ProgramState> superStates = new ArrayList<ProgramState>();
	private ProgrammableSquare target;

	public ProgramState(){
		super();
	}


	@Override
	public void act(ProgrammableSquare subject) {
		for(ProgramAction action:onEnterActions){
			action.act(subject.getSubject(action.targetType(),action));
		}
	}

	@Override
	public int targetType() {
		return 0;
	}
	@Override
	public int getIndex() {
		return 0;
	}

	public void addAction(ProgramAction action) {
		onEnterActions.add(action);
	}
	public List<ProgramAction> getActions(){
		return onEnterActions;
	}

	public void clearEnterActions() {
		onEnterActions.clear();		
	}

	public List<ProgramState> getSubStates(String event) {
		return subStates.get(event);
	}
	public List<ProgramCondition> getSubConditions(String event) {
		return subConditions.get(event);
	}

	public List<ProgramState> getSubStates() {
		List<ProgramState> subStates = new ArrayList<ProgramState>();
		for(String event:eventNames){
			List<ProgramState> forEvent = getSubStates(event);
			if(forEvent!=null&&!forEvent.isEmpty()){
				subStates.addAll(forEvent);
			}
		}
		return subStates;
	}
	public List<String> getEvents() {
		List<String> events = new ArrayList<String>();
		for(String event:eventNames){
			List<ProgramState> forEvent = getSubStates(event);
			if(forEvent!=null&&!forEvent.isEmpty()){
				for(int i=0;i<forEvent.size();++i){
					events.add(event);
				}
			}
		}
		return events;
	}
	public boolean purgeSubState(String event, ProgramState toRemove){
		if(subStates.get(event)==null)return false;
		for(int i=0;i<subStates.get(event).size();++i){
			if(subStates.get(event).get(i)==toRemove){
				subStates.get(event).remove(i);
				subConditions.get(event).remove(i);
				return true;
			}
		}
		return false;
	}
	public boolean purgeSubState(ProgramState toRemove){
		for(String event:eventNames){
			if(purgeSubState(event,toRemove)){
				return true;
			}
		}
		return false;
	}
	public ProgramCondition getSubConditionTo(String event, ProgramState subState) {
		return subConditions.get(event).get(subStates.get(event).indexOf(subState));
	}

	public ProgramCondition getSubConditionTo(ProgramState state) {
		for(String event:subConditions.keySet()){
			int index = subStates.get(event).indexOf(state);
			if(index>-1){
				return subConditions.get(event).get(index);
			}
		}
		return null;
	}
	public String getEventTo(ProgramState state) {
		for(String event:subConditions.keySet()){
			int index = subStates.get(event).indexOf(state);
			if(index>-1){
				return event;
			}
		}
		return null;
	}

	public void changeSubStateEvent(String currentEvent, ProgramState state, String changeToEvent) {
		int index = subStates.get(currentEvent).indexOf(state);
		subStates.get(currentEvent).remove(index);
		ProgramCondition condition = subConditions.get(currentEvent).remove(index);
		addState(changeToEvent, state, condition);
	}
	public void addState(String eventKey, ProgramState state, ProgramCondition condition){
		if(!subStates.containsKey(eventKey)){
			subStates.put(eventKey,new ArrayList<ProgramState>());
			subConditions.put(eventKey,new ArrayList<ProgramCondition>());
		}
		subStates.get(eventKey).add(state);
		subConditions.get(eventKey).add(condition);
		state.superStates.add(this);
	}

	public List<ProgramState> getSuperStates() {
		return superStates;
	}

	@Override
	public void setTarget(ProgrammableSquare target) {
		this.target = target;
	}
	public ProgrammableSquare getTarget() {
		return target;
	}
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		int actionsSize = ints.next();
		for(int i=0;i<actionsSize;++i){
			ProgramAction action = ProgramAction.getAction(ints.next()).create();
			action.setTarget(this);
			onEnterActions.add(action);
			action.loadFrom(ints, floats);
		}
		for(int eventIndex=ints.next();eventIndex>=0;eventIndex=ints.next()){
			subStates.put(ProgramState.eventNames[eventIndex], new ArrayList<ProgramState>());
			subConditions.put(ProgramState.eventNames[eventIndex], new ArrayList<ProgramCondition>());
			int size = ints.next();
			for(int i=0;i<size;++i){
				int index = ints.next();
				//System.out.println(target.getStateIndex(this)+"->"+index);
				ProgramState state = target.getStateFromIndex(index);				
				ProgramCondition condition = ProgramCondition.getCondition(ints.next()).create();
				addState(ProgramState.eventNames[eventIndex],state,condition);
				condition.setState(state);
				condition.loadFrom(ints, floats);
			}
		}
	}

	@Override
	public void saveTo(List<Object> saveTo) {
		saveTo.add(onEnterActions.size());
		for(int i=0;i<onEnterActions.size();++i){
			onEnterActions.get(i).saveTo(saveTo);
		}
		for(int eventIndex=0;eventIndex<ProgramState.eventNames.length;++eventIndex){			
			if(subStates.get(ProgramState.eventNames[eventIndex])!=null&&
					!subStates.get(ProgramState.eventNames[eventIndex]).isEmpty()){
				saveTo.add(eventIndex);
				saveTo.add(subStates.get(ProgramState.eventNames[eventIndex]).size());
				for(int i=0;i<subStates.get(ProgramState.eventNames[eventIndex]).size();++i){
					saveTo.add(target.getStateIndex(subStates.get(ProgramState.eventNames[eventIndex]).get(i)));
					subConditions.get(ProgramState.eventNames[eventIndex]).get(i).saveTo(saveTo);

				}
			}
		}
		saveTo.add(-1);
	}

	private boolean stopLoop = false;
	public void stopLoops(){

		stopLoop = false;
		for(int eventIndex=0;eventIndex<ProgramState.eventNames.length;++eventIndex){			
			if(subStates.get(ProgramState.eventNames[eventIndex])!=null&&
					!subStates.get(ProgramState.eventNames[eventIndex]).isEmpty()){
				for(int i=0;i<subStates.get(ProgramState.eventNames[eventIndex]).size();++i){
					if(subStates.get(ProgramState.eventNames[eventIndex]).get(i).stopLoop){
						subStates.get(ProgramState.eventNames[eventIndex]).get(i).stopLoops();					
					}
				}
			}
		}
	}
	public List<ProgramState> getAllStates(){
		List<ProgramState> allStates = new ArrayList<ProgramState>();
		stopLoops();
		getAllStates(allStates);
		return allStates;
	}
	private void getAllStates(List<ProgramState> builder){
		if(!stopLoop){
			stopLoop = true;
			builder.add(this);
			for(int eventIndex=0;eventIndex<ProgramState.eventNames.length;++eventIndex){			
				if(subStates.get(ProgramState.eventNames[eventIndex])!=null&&
						!subStates.get(ProgramState.eventNames[eventIndex]).isEmpty()){
					for(int i=0;i<subStates.get(ProgramState.eventNames[eventIndex]).size();++i){
						subStates.get(ProgramState.eventNames[eventIndex]).get(i).getAllStates(builder);					
					}
				}
			}
		}
	}
	@Override
	public int saveType() {
		return 6;
	}

	public int size() {
		return subStates.size();
	}

	public void on(String event, ProgrammableSquare advancedSquare) {
		List<ProgramState> stateList = subStates.get(event);
		List<ProgramCondition> conditionList = subConditions.get(event);
		if(stateList==null)return;
		for(int i=0;i<stateList.size();++i){
			if(conditionList.get(i).satisfies(advancedSquare)){
				advancedSquare.setState(stateList.get(i));
				break;
			}
		}
	}


	public ProgramState addSubState(String event) {
		ProgramState subState = new ProgramState();
		subState.setTarget(target);
		FreeProgramCondition subCondition = new FreeProgramCondition();
		subCondition.setState(subState);
		addState(event,subState,subCondition);
		return subState;
	}


	public static String[] getEventNames() {
		return eventNames;
	}




}
