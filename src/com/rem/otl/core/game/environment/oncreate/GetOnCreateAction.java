package com.rem.otl.core.game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

public class GetOnCreateAction extends OnCreateAction {
	private int indexOfList;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		indexOfList=ints.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		List<Object> list = (List<Object>) square.getData().get(indexOfList);
		square.add(list.remove(list.size()-1));
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(indexOfList);
	}
	@Override
	public int getIndex() {
		return 5;
	}
	public OnCreateAction create(){
		return new GetOnCreateAction();
	}
}
