package editor.program;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import editor.Button;
import editor.ButtonAction;
import editor.Editor;
import editor.MapEditor;
import game.Action;
import game.environment.Square;
import game.environment.oncreate.OnCreateAction;
import game.environment.oncreate.OnCreateSquare;
import game.environment.program.action.BaseProgramAction;
import game.environment.program.action.DefineVariableProgramAction;
import game.environment.program.action.DisplayImageProgramAction;
import game.environment.program.action.IncrementVariableProgramAction;
import game.environment.program.action.ProgramAction;
import game.environment.program.action.SetColourProgramAction;
import game.environment.program.action.SetUpdateActionProgramAction;
import game.environment.program.ProgramState;
import game.environment.program.ProgrammableSquare;
import game.environment.program.condition.ProgramCondition;
import game.environment.update.NullUpdateAction;
import game.environment.update.UpdateAction;
import game.hero.Hero;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;
import storage.Storage;

public class ProgramSquareEditor extends Editor implements KeyBoardListener{
	private ProgrammableSquare square;

	private MapEditor editor;

	private StateSquare stateRoot;
	private List<DataRetriever> dataRetrievers = new ArrayList<DataRetriever>();

	public ProgramSquareEditor(MapEditor parent, ProgrammableSquare programSquare){
		super();
		setupButtons();
		Square guide = new Square(15,0,programSquare.getWidth(),programSquare.getHeight());
		guide.reposition(programSquare.getX(),programSquare.getY());
		addChild(guide);
		String text = "";
		squares = Hub.map.getTemplateSquares();
		this.square = programSquare;
		for(Square square:squares){
			for(int i=0;i<square.size();++i){
				if(!(square.getChild(i) instanceof Square)){
					square.removeChild(i);
					--i;
				}
				else {
					addIconsToSquare((Square) square.getChild(i));
				}
			}
			addIconsToSquare(square);
			addChild(square);			
		}

		this.editor = parent;

		for(int i=0;i<ProgramAction.actionNames.size();++i){
			ArrowButton arrow = new ArrowButton(
					this,
					createActionEditor(ProgramAction.getAction(i).create()));
			arrow.reposition(0.82f,
					0.87f-i*0.05f);
			addChild(arrow);
			this.dataRetrievers.add(arrow);
			buttons.add(arrow);
		}

		this.stateRoot = new StateSquare(this,square.getBaseState(),null);
		stateRoot.setupSubStates();
		addChild(this.stateRoot);
		mode = -1;
	}
	@Override
	protected boolean releaseRightMouseButton(MotionEvent e){
		StateSquare removeState = null;
		for(StateSquare state:stateRoot){
			for(int i=0;i<state.numberOfActions();++i){
				if(state.getAction(i).isWithin(e.getX(),e.getY())){
					state.removeAction(i);
					return true;
				}
			}
			if(state.isWithin(e.getX(), e.getY())){
				removeState = state;
				break;
			}
		}
		if(removeState!=null){
			removeState.removeSelf();
			return true;
		}
		return super.releaseRightMouseButton(e);
	}
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(KeyBoardListener.UP==b){
			if(57==keycode){//space
				toggleVisibleSquares();
			}
			else if(44==keycode&&!squares.isEmpty()){
				if(mostRecentlyRemovedSquare!=null){
					addIconsToSquare(mostRecentlyRemovedSquare);
					addChild(mostRecentlyRemovedSquare);
					squares.add(mostRecentlyRemovedSquare);
					mostRecentlyRemovedSquare = null;
				}
				else {
					mostRecentlyRemovedSquare = squares.remove(squares.size()-1);
					removeButtonsFromSquare(mostRecentlyRemovedSquare);
					removeChild(mostRecentlyRemovedSquare);
				}
			}
			else if(45==keycode){
				saveAndReturn();
			}
			else if(keycode==17||keycode==200){//up
				moveView(0,-0.2f);
			}
			else if(keycode==30||keycode==203){//left
				moveView(0.2f,0);
			}
			else if(keycode==31||keycode==208){//down
				moveView(0,0.2f);
			}
			else if(keycode==32||keycode==205){//right
				moveView(-0.2f,0);
			}
			else if(keycode==33){//toggle granity
				granityButton.performOnRelease(null);
			}
		}

	}
	protected void saveCurrent(){
		for(StateSquare state:stateRoot){

			if(state==stateRoot){
				square.setBaseState(state.solidify());
			}
			else {
				state.solidify();
			}
		}
		editor.saveCurrent();
	}
	protected void saveAndReturn() {
		saveCurrent();
		Gui.setView(editor);
	}
	private void moveView(float x, float y){
		for(int i=0;i<squares.size();++i){
			squares.get(i).reposition(squares.get(i).getX()+x*2f,
					squares.get(i).getY()+y*2f);
		}
		stateRoot.moveView(x,y);
	}
	@Override
	public boolean continuousKeyboard() {
		return true;
	}

	@Override
	public boolean handleButtons(MotionEvent e){
		boolean successful = super.handleButtons(e);
		for(DataRetriever retriever:dataRetrievers){
			retriever.retrieveData();
		}
		return successful;
	}
	public ActionEditor createActionEditor(ProgramAction action){
		ActionEditor ae = null;
		if(action instanceof BaseProgramAction){
			ae = new ActionEditor(this,"editor_button",1,action,null){
				@Override
				public boolean retrieveData() {
					return false;
				}};
		}
		else if(action instanceof SetColourProgramAction){
			ae = new SquareActionEditor(this,"squares",blackColour,action, (SetColourProgramAction)action){
				@Override
				public boolean retrieveData(){

					if((Integer)action.getData("heroColourToChange")==Hero.BLACK_INT&&blackColour!=frame){
						setFrame(blackColour);
						this.action.setData("subject", blackColour);
						return true;
					}
					else if((Integer)action.getData("heroColourToChange")==Hero.WHITE_INT&&whiteColour!=frame){
						setFrame(whiteColour);
						this.action.setData("subject", whiteColour);
						return true;
					}
					else return false;
				}
			};				
			ae.setFrame((Integer) action.getData("subject"));
		}
		else if(action instanceof SetUpdateActionProgramAction){
			int updateAction = ((UpdateAction) action.getData("subject")).getIndex();
			ae = new ActionEditor(this,"editor_update_icons",updateAction,action,(UpdateAction)action.getData("subject")){
				@Override
				public boolean retrieveData(){
					int updateAction = getFirstUpdateAction();
					if(((UpdateAction)target).getIndex()!=updateAction){
						UpdateAction newAction = UpdateAction.getAction(updateAction);
						if(newAction==null){
							newAction = new NullUpdateAction();
						}
						else {
							newAction = newAction.create();
						}
						for(int valueId:target.copiableValueIds()){
							newAction.setValue(valueId,target.getValue(valueId));
						}
						for(int valueId:target.copiableIntIds()){
							newAction.setValue(valueId,target.getInt(valueId));
						}
						this.action.setData("subject", newAction);
						this.target = newAction;
						this.setFrame(updateAction);
						return true;
					}
					else return false;
				}

			};
		}
		else if(action instanceof DefineVariableProgramAction){
			ae = new ActionEditor(this,"editor_program_icons",0,action,(DefineVariableProgramAction)action){
				@Override
				public boolean retrieveData(){
					return true;
				}

			};
		}
		else if(action instanceof IncrementVariableProgramAction){
			ae = new ActionEditor(this,"editor_program_icons",1,action,(IncrementVariableProgramAction)action){
				@Override
				public boolean retrieveData(){
					return true;
				}

			};
		}
		else if(action instanceof DisplayImageProgramAction){
			ae = new ActionEditor(this,"editor_program_icons",2,action,(DisplayImageProgramAction)action){
				@Override
				public boolean retrieveData(){
					return true;
				}

			};
		}
		return ae;
	}
	public int getFirstUpdateAction(){
		for(int i=0;i<updateActionMenu.size();++i){
			if(updateActionMenu.get(i).isSelected()){
				return i;
			}
		}
		return -1;
	}
	public void addTransitioningActionEditor(ActionEditor editor){
		addChild(editor);
	}
	public void removeTransitioningActionEditor(ActionEditor editor){
		buttons.remove(editor);
		removeChild(editor);
	}





	public StateSquare getStateRoot() {
		return stateRoot;
	}
	public void addButton(Button button) {
		this.buttons.add(button);
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	@Override
	protected void openNew() {

	}
	@Override
	public void update(double secondsSinceLastFrame){
		super.update(secondsSinceLastFrame);
		for(StateSquare square1:stateRoot){
			for(StateSquare square2:stateRoot){
				if(square1==square2)continue;
				if(square2.getY()==square1.getY()){
					square2.reposition(square2.getX(), square2.getY()-0.001f);
				}
				else if(square2.getX()<=square1.getX()+square1.getWidth()/2f&&
						square2.getX()+square2.getWidth()>=square1.getX()+square1.getWidth()/2f&&
						square2.getY()<=square1.getY()+square1.getHeight()+0.05f&&
						square2.getY()+square2.getHeight()>=square1.getY()+square1.getHeight()){
					square2.reposition(square2.getX(), square2.getY()+0.0005f);
					square1.reposition(square1.getX(), square1.getY()-0.0005f);
				}/*
				else if(square2.isWithin(square1.getX()+square1.getWidth()/2f,square1.getY())){
					square2.reposition(square2.getX(), square2.getY()-0.0005f);
					square1.reposition(square1.getX(), square1.getY()+0.0005f);
				}*/
			}
			square1.reposition(square1.getX(), square1.getY());
		}
	}
	public StateSquare getStateSquare(ProgramState subState) {
		for(StateSquare square:stateRoot){
			if(square.getState()==subState){
				return square;
			}
		}
		return null;
	}
	public StateSquare getStateSquare(float x, float y) {
		for(StateSquare square:stateRoot){
			if(square.isWithin(x, y)){
				return square;
			}
		}
		return null;
	}
}
