package editor.program;

import game.environment.program.ProgramAction;

public abstract class SquareActionEditor extends ActionEditor {
	public SquareActionEditor(ProgramSquareEditor editor,String textureName, Integer frame, ProgramAction action) {
		super(editor,textureName, frame, action);
	}
	@Override
	public float offsetX(int i){
		if(i==0){
			return frame>=0?getWidth()*0.1875f:0f;
		}
		else return super.offsetX(i);
	}
	@Override
	public float offsetY(int i){
		if(i==0){
			return frame>=0?getHeight()*0.1875f:0f;
		}
		else return super.offsetY(i);
	}
	@Override
	public void resize(float dx, float dy){
		super.resize(dx,dy);
		getIcon().resize(dx*0.625f,dy*0.625f);
	}



}
