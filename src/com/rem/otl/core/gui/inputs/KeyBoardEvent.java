package com.rem.otl.core.gui.inputs;

public class KeyBoardEvent implements InputEvent{

	public static final boolean KEY_DOWN = true;
	public static final boolean KEY_UP = false;
	

	public static final int ESCAPE = 1;
	public static final int SPACE = 57;
	public static final int ENTER = 28;
	public static final int BACKSPACE = 14;
	public static final int UP = 200;
	public static final int LEFT = 203;
	public static final int DOWN = 208;
	public static final int RIGHT = 205;
	public static final int CTRL_LEFT = 29;
	public static final int CTRL_RIGHT = 157;
	public static final int END = 207;
	public static final int DELETE = 211;
	private boolean upDown;
	private char characterRepresentation;
	private int intRepresentation ;
	public KeyBoardEvent(boolean upDown,char characterRepresentation, int intRepresentation){
		this.upDown = upDown;
		this.characterRepresentation = characterRepresentation;
		this.intRepresentation = intRepresentation;
		
	}
	
	public boolean keyDown(){
		return upDown==KEY_DOWN;
	}
	public boolean keyUp(){
		return upDown==KEY_UP;
	}
	public char getChar(){
		return characterRepresentation;
	}
	public int getKeyCode(){
		return intRepresentation;
	}
	
	@Override
	public int getType() {
		return InputEvent.KEYBOARD;
	}

	public boolean is(int keyCode) {
		return this.intRepresentation==keyCode;
	}

}
