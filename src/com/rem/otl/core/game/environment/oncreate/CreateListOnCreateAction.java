package com.rem.otl.core.game.environment.oncreate;

import java.util.ArrayList;

public class CreateListOnCreateAction extends OnCreateAction {

	@Override
	public void act(OnCreateSquare square) {
		square.add(new ArrayList<Object>());
	}
	@Override
	public int getIndex() {
		return 1;
	}
	public OnCreateAction create(){
		return new CreateListOnCreateAction();
	}
}
