package com.rem.otl.core.game.environment.update;

public class NullUpdateAction extends UpdateAction{
	public NullUpdateAction(){
		defaultState = true;
	}
	@Override
	public int getIndex() {
		return -1;
	}

	@Override
	public void act(Double seconds) {
	}

	@Override
	public UpdateAction create() {
		return new NullUpdateAction();
	}

}
