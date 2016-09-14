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
import game.environment.program.ChangeColourProgramAction;
import game.environment.program.ProgramAction;
import game.environment.program.ProgramState;
import game.environment.program.ProgrammableSquare;
import game.environment.program.condition.ProgramCondition;
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
			ArrowButton arrow = new ArrowButton(createActionEditor(ProgramAction.getAction(i).create()));
			arrow.reposition(0.03f,
					     0.93f-i*0.05f);
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
				if(visibleTo==0){
					visibleTo = 1;
					visibleToShower.setFrame(0);
				}
				else if(visibleTo==1){
					visibleTo = 2;
					visibleToShower.setFrame(1);
				}
				else if(visibleTo==2){
					visibleTo = 0;
					visibleToShower.setFrame(3);
				}
				setVisibleSquares(visibleTo);
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
				saveAndReturnToEditor();
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
				granityShower.setFrame(granityShower.getFrame()==0?1:0);
			}
		}

	}
	protected void saveAndReturnToEditor() {
		square.setBaseState(stateRoot.solidify());
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
			ae = new ActionEditor(this,"editor_button",1,action){
				@Override
				public boolean retrieveData() {
					return false;
				}};
		}
		else if(action instanceof ChangeColourProgramAction){
			if((Boolean)action.getData("heroColourToChange")){
				ae = new SquareActionEditor(this,"squares",blackColour,action){
					@Override
					public boolean retrieveData(){
						if(blackColour!=this.getIcon().getFrame()){
							this.getIcon().setFrame(blackColour);
							this.action.setData("subject", blackColour);
							return true;
						}
						else return false;
					}
				};				
			}
			else {
				ae = new SquareActionEditor(this,"squares",whiteColour,action){
					@Override
					public boolean retrieveData(){
						if(whiteColour!=this.getIcon().getFrame()){
							this.getIcon().setFrame(whiteColour);
							this.action.setData("subject", whiteColour);
							return true;
						}
						else return false;
					}
				};
			}
			ae.getIcon().setFrame((Integer) action.getData("subject"));
		}
		return ae;
	}
	public void addTransitioningActionEditor(ActionEditor editor){
		addChild(editor);
	}
	public void removeTransitioningActionEditor(ActionEditor editor){
		buttons.remove(editor);
		removeChild(editor);
	}


	private class ArrowButton extends Button implements DataRetriever{

		private ActionEditor actionEditor;
		private GraphicEntity subject;
		public ArrowButton(final ActionEditor actionEditor) {
			super("editor_button",3, null);
			this.actionEditor = actionEditor;
			subject = new GraphicEntity(actionEditor.getIcon().getTextureName(), 1);
			subject.setFrame(actionEditor.getIcon().getFrame());
			this.addChild(subject);
			resize(0.1f,0.05f,0.05f,0.05f);

		}
		@Override
		public void performOnClick(MotionEvent e){
			ActionEditor transitioner = createActionEditor(actionEditor.action);					
			transitioner.reposition(e.getX()-getWidth()/2f,
					            e.getY()-getHeight()/2f);
			transitioner.resize(0.025f,0.025f);
			transitioner.retrieveData();
			addTransitioningActionEditor(transitioner);
			Gui.giveOnClick(transitioner);

		}
		@Override
		public boolean retrieveData(){
			if(actionEditor.retrieveData()){
				subject.setFrame(actionEditor.getIcon().getFrame());
				return true;
			}
			else return false;
		}
		@Override
		public float offsetX(int index){
			if(index>0){
				return getWidth()+0.005f;
			}
			else return super.offsetX(index);
		}
		@Override
		public void resize(float x1, float y1, float x2, float y2){
			super.resize(x1,y1);
			subject.resize(x2, y2);
		}
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
}
