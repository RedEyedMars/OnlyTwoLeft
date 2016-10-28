package com.rem.otl.core.gui.inputs;


public class ClickEvent implements InputEvent{

	public static final int ACTION_DOWN = 0;	
	public static final int ACTION_MOVE = 1;
	public static final int ACTION_UP = 2;

	public static final int MOUSE_LEFT = 0;
	public static final int MOUSE_RIGHT = 1;

	private float x;

	private float y;

	private int action;

	private int button = 0;

	public ClickEvent(float x, float y, int action) {
		this.x = x;
		this.y = y;
		this.action = action;
	}

	public ClickEvent(float x, float y, int action, int button) {
		this(x,y,action);
		this.button = button;
	}

	public int getAction() {
		return action;
	}

	public int getButton(){
		return button;
	}

	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}

	public void setAction(int action) {
		this.action = action;
	}

	@Override
	public int getType() {
		return InputEvent.CLICK;
	}
}
