package com.rem.otl.core.editor.field;

public abstract class IntegerFieldComponent <TargetType extends Object> extends TextFieldComponent<TargetType,Integer>{

	public IntegerFieldComponent(String font) {
		super(font);	
	}

	protected void advance(String text){
		act(Integer.parseInt(text));
	}
	protected boolean legalKey(char c, int keycode){
		return c>=48&&c<=57||c==45||keycode==14;
	}
}
