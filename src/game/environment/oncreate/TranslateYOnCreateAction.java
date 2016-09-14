package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import gui.graphics.GraphicEntity;

public class TranslateYOnCreateAction extends OnCreateAction {

	private float dy;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		dy=floats.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		GraphicEntity toTranslate = (GraphicEntity) square.getLast();
		toTranslate.reposition(toTranslate.getX(),toTranslate.getY()+dy);
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(dy);
	}
	@Override
	public int numberOfFloats(){
		return 1;
	}
	@Override
	public int getIndex() {
		return 12;
	}
	public OnCreateAction create(){
		return new TranslateYOnCreateAction();
	}
}
