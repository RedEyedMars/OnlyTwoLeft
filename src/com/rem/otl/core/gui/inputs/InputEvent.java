package com.rem.otl.core.gui.inputs;

public interface InputEvent {
	public static final int CLICK = 0;
	public static final int KEYBOARD = 1;
	public static final int WHEEL = 2;
	public static final int HOVER = 3;
	
	public int getType();
}
