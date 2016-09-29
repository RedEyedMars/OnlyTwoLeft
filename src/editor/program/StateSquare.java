package editor.program;

import java.util.ArrayList;
import java.util.List;

import editor.Button;
import editor.ButtonAction;
import game.environment.program.ProgramAction;
import game.environment.program.ProgramState;
import game.environment.program.condition.FreeProgramCondition;
import gui.inputs.MotionEvent;

public class StateSquare extends Button{
	private ProgramState state;

	private ConditionArrow condition;
	private StateSquare parentSquare = null;
	private List<StateSquare> subStates = new ArrayList<StateSquare>();
	private List<ActionEditor> actions = new ArrayList<ActionEditor>();

	private ProgramSquareEditor editor;

	private Button linkNewStateButton;

	private static final float EMPTY_WIDTH = 0.125f;
	private static final float EMPTY_HEIGHT = 0.075f;

	public StateSquare(final ProgramSquareEditor editor,ProgramState programState, String event) {
		super("squares",2,"",null,null);
		this.editor=editor;
		this.state = programState;
		resize(0f,0f);
		reposition(0.05f,0.5f);
		int i=0;
		for(String subEvent:ProgramState.eventNames){
			if(state.getSubStates(subEvent)!=null){
				for(ProgramState subState:state.getSubStates(subEvent)){
					StateSquare ss = new StateSquare(editor,subState,subEvent);
					addChild(ss);
					subStates.add(ss);
					ss.parentSquare=this;
					if(i==0){
						ss.reposition(getX()+getWidth()+0.15f, getY());
					}
					else {
						ss.reposition(getX()+getWidth()+0.15f, subStates.get(i-1).getY()+subStates.get(i-1).getHeight()+0.05f);
					}
					++i;
				}
			}
		}
		reposition(0.05f,0.5f);
		for(ProgramAction action:state.getActions()){
			ActionEditor ae = editor.createActionEditor(action);
			ae.reposition(getX()+0.01f,getY()+0.01f);
			addActionEditor(ae);
		}
		if(event!=null){
			condition = new ConditionArrow(editor,state.getCondition(),event);
			addChild(condition);
		}
		
		final StateSquare self = this;
		this.linkNewStateButton = new Button("editor_shape_icons",1,"Create a new State and link it to this state",null,new ButtonAction(){
			@Override
			public void act(MotionEvent event) {
				ProgramState subState = state.addSubState("update");
				StateSquare ss = new StateSquare(editor,subState,"update");
				self.addChild(ss);
				subStates.add(ss);
				ss.parentSquare = self;
				if(subStates.size()>1){
					ss.reposition(self.getX()+self.getWidth()+0.15f, subStates.get(subStates.size()-1-1).getY()+subStates.get(subStates.size()-1-1).getHeight()+0.05f);
				}
				else {
					ss.reposition(self.getX()+self.getWidth()+0.15f, self.getY());
				}
				ss.resize(0f, 0f);
				self.reposition(self.getX(),self.getY());
			}
		});
		addChild(linkNewStateButton);		
		editor.addButton(linkNewStateButton);
		resize(0f,0f);
		reposition(getX(),getY());
	}
	public ProgramState solidify() {
		ProgramState state = new ProgramState();
		if(condition!=null){
			state.setCondition(condition.getCondition());
		}
		else {
			state.setCondition(new FreeProgramCondition());
		}
		for(ActionEditor action:actions){
			state.addAction(action.action);
		}
		for(StateSquare subState:subStates){
			state.addState(subState.condition.getEvent(), subState.solidify());
		}
		return state;
	}
	public void moveView(float x, float y) {
		reposition(getX()+x,
				getY()+y);
		for(StateSquare state:subStates){
			state.moveView(x, y);
		}
	}
	@Override
	public float offsetX(int index){
		if(getChild(index) instanceof StateSquare){
			return getChild(index).getX()-getX();
		}
		else if(getChild(index) instanceof ActionEditor){
			return 0.065f;
		}
		else if(getChild(index) instanceof ConditionArrow&&parentSquare!=null){
			return parentSquare.getX()+parentSquare.getWidth()-getX();
		}
		else if(getChild(index) == linkNewStateButton){
			return getWidth();
		}
		else return 0f;
	}
	@Override
	public float offsetY(int index){
		if(getChild(index) instanceof StateSquare){
			return getChild(index).getY()-getY();
		}
		else if(getChild(index) instanceof ActionEditor){
			int ai = actions.indexOf(getChild(index));
			return getHeight()-0.015f-ai*0.026f-getChild(index).getHeight();
		}
		else if(getChild(index) instanceof ConditionArrow&&parentSquare!=null){
			return parentSquare.getY()+parentSquare.getHeight()/2f-getY();
		}
		else if(getChild(index) == linkNewStateButton){
			return getHeight()/2f;
		}
		else return 0f;
	}
	@Override
	public void resize(float x, float y){
		super.resize(EMPTY_WIDTH, EMPTY_HEIGHT+0.026f*actions.size());
		if(condition!=null&&parentSquare!=null){
			condition.resize(getX()-(parentSquare.getX()+parentSquare.getWidth()), 
					(getY()+getHeight()/2f)-(parentSquare.getY()+parentSquare.getHeight()/2f));
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
			resize(0f,0f);
			reposition(getX(),getY());
			return this;
		}
		for(StateSquare state:subStates){
			state.addActionEditor(actionEditor);
		}
		return null;
	}
}
