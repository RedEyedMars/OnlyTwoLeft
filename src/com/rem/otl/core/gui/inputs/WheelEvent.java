package com.rem.otl.core.gui.inputs;

public class WheelEvent implements InputEvent{
	
	private int amount;

	public WheelEvent(int amount){
		this.amount = amount;
	}
	
	public int getAmount(){
		return amount;
	}

	@Override
	public int getType() {
		return InputEvent.WHEEL;
	}

}
