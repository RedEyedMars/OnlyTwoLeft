package editor.field;

import gui.Gui;
import gui.inputs.KeyBoardListener;

public abstract class FloatFieldComponent <TargetType extends Object> extends TextFieldComponent<TargetType,Float>{

	public FloatFieldComponent(String font) {
		super(font);
	}
	protected void advance(String text){
		try {
			act(Float.parseFloat(getText()));
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	protected boolean legalKey(char c, int keycode){
		return c>=48&&c<=57||c==46||c==45||keycode==14;
	}
}
