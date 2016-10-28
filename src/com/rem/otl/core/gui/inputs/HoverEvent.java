package com.rem.otl.core.gui.inputs;


public class HoverEvent implements InputEvent{

	private float x;

	private float y;

	public HoverEvent(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}

	@Override
	public int getType() {
		return InputEvent.HOVER;
	}
}
