package editor.field;

import editor.TextWriter;
import game.Action;
import gui.Gui;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;

public abstract class TextFieldComponent<TargetType extends Object,SubjectType extends Object> extends TextWriter implements Action<SubjectType>{

	protected TextWriter next=null;
	protected TargetType target;
	protected FieldEditor<TargetType> parentField;
	public TextFieldComponent(String font) {
		super(font, " ");	
		charIndex=0;
		index=0;
	}

	public void setNext(TextWriter next) {
		this.next = next;
	}
	public void setTarget(TargetType target) {
		this.target=target;
	}
	public void setParent(FieldEditor<TargetType> parentField) {
		this.parentField = parentField;
	}
	@Override
	public void keyCommand(boolean b, char c, int keycode){
		if(legalKey(c,keycode)){
			super.keyCommand(b, c, keycode);
		}
		else if(b==KeyBoardListener.UP&&(keycode==15||keycode==28)){
			advance(this.getText());
			Gui.removeOnType(this);
			if(next!=null){
				Gui.giveOnType(next);
				this.parentField.nextType();
			}
			else {
				Gui.removeOnClick(parentField);
				parentField.setVisible(false);
			}
		}
	}
	protected abstract boolean legalKey(char c, int keycode);
	protected abstract void advance(String text);

	public abstract void updateWith(TargetType subject);

}
