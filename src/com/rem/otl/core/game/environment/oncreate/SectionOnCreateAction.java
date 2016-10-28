package com.rem.otl.core.game.environment.oncreate;

import java.util.List;

public class SectionOnCreateAction extends BlockOnCreateAction{
	@Override
	public void act(OnCreateSquare square) {
		square.getData().clear();
		for(OnCreateAction action:actions){
			action.act(square);
		}
	}
	@Override
	public void saveTo(List<Object> saveTo){
		super.saveArgs(saveTo);
	}
	@Override
	public int numberOfInts(){
		return 1;
	}
	@Override
	public int getIndex() {
		return 0;
	}
	
	public OnCreateAction create(){
		return new SectionOnCreateAction();
	}
}
