package editor.program;

import editor.Button;
import editor.ButtonAction;
import game.environment.program.ProgramAction;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;

public abstract class ActionEditor extends Button implements DataRetriever, Cloneable{
	protected ProgramAction action;
	private StateSquare state = null;

	private GraphicEntity arrow;
	protected ProgramSquareEditor editor;

	public ActionEditor(ProgramSquareEditor editor,String textureName, Integer frame, final ProgramAction action) {
		super(textureName, frame, null);
		this.editor = editor;
		final ActionEditor self = this;
		this.action = action;
		this.arrow = new GraphicEntity("editor_button",1);
		this.arrow.setFrame(3);
		addChild(arrow);			
		setAction(new ButtonAction(){
			@Override
			public void act(Object subject) {
				self.retrieveData();
				//Summon UpdateStyle updateField
			}});
	}

	@Override
	public boolean onClick(MotionEvent e){
		if(MotionEvent.ACTION_UP==e.getAction()){
			if(state==null){
				reposition(e.getX()-getWidth()/2f,
					 e.getY()-getHeight()/2f);
				editor.removeTransitioningActionEditor(this);
				this.state = editor.getStateRoot().addActionEditor(this);
				Gui.removeOnClick(this);
			}
		}
		return true;
	}

	public boolean onHover(MotionEvent e){
		reposition(e.getX()-getWidth()/2f,
			   e.getY()-getHeight()/2f);
		return true;
	}
	@Override
	public float offsetX(int index){
		if(index>0){
			return -getChild(index).getWidth()-0.005f;
		}
		else {
			return super.offsetX(index);
		}
	}
	@Override
	public void resize(float dx, float dy){
		super.resize(dx,dy);
		arrow.resize(dx*2f,dy);
	}
}
