package com.rem.otl.core.game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import com.rem.otl.core.gui.graphics.GraphicEntity;

public class TranslateXOnCreateAction extends OnCreateAction {

	private float dx;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		dx=floats.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		GraphicEntity toTranslate = (GraphicEntity) square.getLast();
		toTranslate.reposition(toTranslate.getX()+dx,toTranslate.getY());
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(dx);
	}
	@Override
	public int numberOfFloats(){
		return 1;
	}
	@Override
	public int getIndex() {
		return 11;
	}
	public OnCreateAction create(){
		return new TranslateXOnCreateAction();
	}
}
