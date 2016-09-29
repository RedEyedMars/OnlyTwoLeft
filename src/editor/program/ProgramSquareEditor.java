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
import game.environment.program.BaseProgramAction;
import game.environment.program.SetColourProgramAction;
import game.environment.program.SetUpdateActionProgramAction;
import game.environment.program.ProgramAction;
import game.environment.program.ProgramState;
import game.environment.program.ProgrammableSquare;
import game.environment.program.condition.ProgramCondition;
import game.environment.update.NullUpdateAction;
import game.environment.update.UpdateAction;
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
		addChild(this.stateRoot);
		mode = -1;
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
		square.setBaseState(stateRoot.solidify());
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

						if((Boolean)action.getData("heroColourToChange")&&blackColour!=frame){
							setFrame(blackColour);
							this.action.setData("subject", blackColour);
							return true;
						}
						else if(!(Boolean)action.getData("heroColourToChange")&&whiteColour!=frame){
							setFrame(whiteColour);
							this.action.setData("subject", whiteColour);
							return true;
						}
						else return false;
					}
				};				
			
			ae.setFrame((Integer) action.getData("subject"));		}

		else if(action instanceof SetUpdateActionProgramAction){
			int updateAction = ((UpdateAction) action.getData("subject")).getIndex();
			ae = new ActionEditor<UpdateAction>(this,"editor_update_icons",updateAction,action,(UpdateAction)action.getData("subject")){
				@Override
				public boolean retrieveData(){
					int updateAction = getFirstUpdateAction();
					if(target.getIndex()!=updateAction){
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
}
