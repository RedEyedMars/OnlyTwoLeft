package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import gui.graphics.GraphicEntity;

public class TranslateXOnCreateAction extends OnCreateAction {

	private float dx;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		dx=floats.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		GraphicEntity toTranslate = (GraphicEntity) square.getLast();
		toTranslate.setX(toTranslate.getX()+dx);
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
