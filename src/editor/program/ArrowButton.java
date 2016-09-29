package editor.program;

import editor.Button;
import editor.ButtonAction;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;

public class ArrowButton extends Button implements DataRetriever{

	private ActionEditor actionEditor;
	private GraphicEntity subject;
	private boolean transitioning;
	public ArrowButton(final ProgramSquareEditor editor, final ActionEditor actionEditor) {
		super("editor_arrows",0,"Create"+actionEditor.getDescription(),null,null);
		final ArrowButton self = this;
		this.actionEditor = actionEditor;
		setOnClick(new ButtonAction(){
			@Override
			public void act(MotionEvent event){
				if(!transitioning){
					transitioning = true;
					ActionEditor transitioner = editor.createActionEditor(actionEditor.action);
					transitioner.setParentArrowButton(self);
					transitioner.reposition(event.getX()-0.05f,
							event.getY()-0.05f);
					transitioner.resize(0.025f,0.025f);
					transitioner.retrieveData();
					editor.addTransitioningActionEditor(transitioner);
					Gui.giveOnClick(transitioner);
				}
			}
		});
		subject = new GraphicEntity(actionEditor.getIcon().getTextureName(), 1);
		subject.setFrame(actionEditor.getIcon().getFrame());
		this.addChild(subject);
		resize(0.1f,0.05f,0.05f,0.05f);

	}		
	@Override
	public boolean retrieveData(){
		if(actionEditor.retrieveData()){
			if(actionEditor.getIcon().getFrame()>=0){
				subject.setFrame(actionEditor.getIcon().getFrame());
			}
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
	public void free() {
		transitioning = false;
	}
}
