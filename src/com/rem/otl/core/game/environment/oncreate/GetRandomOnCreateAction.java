package com.rem.otl.core.game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import com.rem.otl.core.main.Hub;

public class GetRandomOnCreateAction extends OnCreateAction {

	private int indexOfList;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		indexOfList=ints.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		List<Object> list = (List<Object>) square.getData().get(indexOfList);
		square.add(list.remove((int)(Hub.randomizer.nextFloat()*list.size())));
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(indexOfList);
	}
	@Override
	public int getIndex() {
		return 6;
	}
	public OnCreateAction create(){
		return new GetRandomOnCreateAction();
	}
}
