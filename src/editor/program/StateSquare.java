package editor.program;

import java.util.ArrayList;
import java.util.List;

import editor.Button;
import editor.ButtonAction;
import game.environment.program.ProgramAction;
import game.environment.program.ProgramState;
import gui.inputs.MotionEvent;

public class StateSquare extends Button{
	private ProgramState state;
	
	private ConditionArrow condition;		
	private List<StateSquare> subStates = new ArrayList<StateSquare>();
	private List<ActionEditor> actions = new ArrayList<ActionEditor>();

	private ProgramSquareEditor editor;
	public StateSquare(ProgramSquareEditor editor,ProgramState state, String event) {
		super("squares",2,new ButtonAction(){public void act(Object subject) {}});
		this.editor=editor;
		this.state = state;
		adjust(0.125f,0.075f);
		for(String subEvent:ProgramState.eventNames){
			if(state.getSubStates(subEvent)!=null){
				for(ProgramState subState:state.getSubStates(subEvent)){
					StateSquare ss = new StateSquare(editor,subState,subEvent);
					addChild(ss);
					ss.onAddToDrawable();
					subStates.add(ss);						
				}
			}
		}
		setX(0.05f);
		setY(0.5f);
		for(ProgramAction action:state.getActions()){
			ActionEditor ae = editor.createActionEditor(action);
			ae.setX(getX()+0.01f);
			ae.setY(getY()+0.01f);
			addActionEditor(ae);
		}
		condition = new ConditionArrow(state.getCondition(),event);
		addChild(condition);
		adjust(getWidth(),getHeight());
		setX(getX());
		setY(getY());
		editor.addButton(this);
	}
	public ProgramState solidify() {
		ProgramState state = new ProgramState();
		state.setCondition(condition.getCondition());
		for(ActionEditor action:actions){
			state.addAction(action.action);
		}
		for(StateSquare subState:subStates){
			state.addState(subState.condition.getEvent(), subState.solidify());
		}
		return state;
	}
	@Override
	public void performOnClick(MotionEvent e){
		editor.setMode(-1);
		ProgramState subState = state.addSubState("update");
		StateSquare ss = new StateSquare(editor,subState,"update");
		addChild(ss);
		ss.onAddToDrawable();
		subStates.add(ss);
		setX(getX());
		setY(getY());
	}
	public void moveView(float x, float y) {
		setX(getX()+x);
		setY(getY()+y);
		for(StateSquare state:subStates){
			state.moveView(x, y);
		}
	}
	@Override
	public float offsetX(int index){
		if(getChild(index) instanceof StateSquare){
			return getWidth()+0.05f;
		}
		else if(getChild(index) instanceof ActionEditor){
			return 0.065f;
		}
		else if(getChild(index) instanceof ConditionArrow){
			return -0.05f;
		}
		else return 0f;
	}
	@Override
	public float offsetY(int index){
		if(getChild(index) instanceof StateSquare){
			float dy = 0f;
			int ssi = subStates.indexOf(getChild(index));
			if(ssi>0){
				dy=subStates.get(ssi-1).getY()+subStates.get(ssi-1).getHeight()+0.005f;
			}
			if(ssi<subStates.size()-2||subStates.size()<2){
				return dy;
			}
			else {
				subStates.get(ssi).setY(getY()+dy);
				float dy2 = (subStates.get(ssi).getY()+subStates.get(ssi).getHeight()-
						subStates.get(0).getY())/2f;   
				for(int i=0;i<ssi;++i){
					subStates.get(i).setY(subStates.get(i).getY()-dy2);
				}
				return dy2;
			}
		}
		else if(getChild(index) instanceof ActionEditor){
			int ai = actions.indexOf(getChild(index));
			return getHeight()-0.015f-ai*0.026f-getChild(index).getHeight();
		}
		else if(getChild(index) instanceof ConditionArrow){
			return getHeight()/2f-getChild(index).getHeight()/2f;
		}
		else return 0f;
	}
	@Override
	public void adjust(float x, float y){
		super.adjust(x, y);
		if(condition!=null){
			condition.adjust(0.05f, 0.025f);
		}
		for(ActionEditor action:actions){
			action.adjust(0.025f,0.025f);
		}
	}
	public StateSquare addActionEditor(ActionEditor actionEditor) {
		if(isWithin(actionEditor.getX(), actionEditor.getY())){	
			ActionEditor action = editor.createActionEditor(actionEditor.action);
			addChild(action);
			action.onAddToDrawable();
			actions.add(action);
			editor.addButton(action);
			adjust(0.125f,getHeight()+0.026f);
			setX(getX());
			setY(getY()-0.026f);
			return this;
		}
		for(StateSquare state:subStates){
			state.addActionEditor(actionEditor);
		}
		return null;
	}
}
