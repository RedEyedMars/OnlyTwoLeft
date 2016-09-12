package game.environment.program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import game.environment.SquareAction;
import game.environment.program.condition.Condition;
import game.environment.program.condition.ProgramCondition;

public class ProgramState implements SquareAction<ProgrammableSquare,ProgrammableSquare>, Condition<ProgrammableSquare>{
	private static final long serialVersionUID = 5712159876488441511L;
	public static final String[] eventNames = new String[]{"stepBlack","stepWhite","update","limitReached"};
	private ProgramCondition condition;
	private List<ProgramAction> onEnterActions = new ArrayList<ProgramAction>();
	private LinkedHashMap<String,List<ProgramState>> subStates = new LinkedHashMap<String,List<ProgramState>>();
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

	public ProgramCondition getCondition() {
		return condition;
	}

	public void setCondition(ProgramCondition condition) {
		this.condition = condition;
	}



	public void addAction(ProgramAction action) {
		onEnterActions.add(action);
	}
	public List<ProgramAction> getActions(){
		return onEnterActions;
	}
	
	public List<ProgramState> getSubStates(String event) {
		return subStates.get(event);
	}
	public void addState(String eventKey, ProgramState state){
		if(!subStates.containsKey(eventKey)){
			subStates.put(eventKey,new ArrayList<ProgramState>());		
		}
		subStates.get(eventKey).add(state);
	}
	@Override
	public void setTarget(ProgrammableSquare target) {
		this.target = target;
	}
	public ProgrammableSquare getTarget() {
		return target;
	}
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		condition = ProgramCondition.getCondition(ints.next()).create();
		condition.setState(this);
		condition.loadFrom(ints, floats);
		int actionsSize = ints.next();
		for(int i=0;i<actionsSize;++i){
			ProgramAction action = ProgramAction.getAction(ints.next()).create();
			action.setTarget(this);
			onEnterActions.add(action);
			action.loadFrom(ints, floats);
		}
		for(int eventIndex=ints.next();eventIndex>=0;eventIndex=ints.next()){
			subStates.put(ProgramState.eventNames[eventIndex], new ArrayList<ProgramState>());
			int size = ints.next();
			for(int i=0;i<size;++i){
				ProgramState state = new ProgramState();
				state.setTarget(target);
				subStates.get(ProgramState.eventNames[eventIndex]).add(state);
				state.loadFrom(ints, floats);
			}
		}
	}

	@Override
	public void saveTo(List<Object> saveTo) {
		condition.saveTo(saveTo);
		saveTo.add(onEnterActions.size());
		for(int i=0;i<onEnterActions.size();++i){
			onEnterActions.get(i).saveTo(saveTo);
		}
		for(int eventIndex=0;eventIndex<ProgramState.eventNames.length;++eventIndex){			
			if(subStates.get(ProgramState.eventNames[eventIndex])!=null&&
			   !subStates.get(ProgramState.eventNames[eventIndex]).isEmpty()){
				saveTo.add(eventIndex);
				saveTo.add(subStates.get(ProgramState.eventNames[eventIndex]).size());
				for(ProgramState action:subStates.get(ProgramState.eventNames[eventIndex])){
					action.saveTo(saveTo);
				}
			}
		}
		saveTo.add(-1);
	}
	@Override
	public int saveType() {
		return 6;
	}

	@Override
	public boolean satisfies(ProgrammableSquare subject) {
		return condition.satisfies(subject.getSubject(condition.targetType(), condition));
	}

	public int size() {
		return subStates.size();
	}

	public void on(String event, ProgrammableSquare advancedSquare) {
		List<ProgramState> stateList = subStates.get(event);
		if(stateList==null)return;
		for(int i=0;i<stateList.size();++i){
			if(stateList.get(i).satisfies(advancedSquare)){
				stateList.get(i).act(advancedSquare);
				advancedSquare.setState(stateList.get(i));
				break;
			}
		}
	}


	public ProgramState addSubState(String event) {
		ProgramState subState = new ProgramState();
		subState.setTarget(target);
		subState.condition = ProgramCondition.getCondition(0).create();
		subState.condition.setState(subState);
		addState(event,subState);
		return subState;
	}




}
