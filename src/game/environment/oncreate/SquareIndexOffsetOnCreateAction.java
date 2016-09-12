package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

public class SquareIndexOffsetOnCreateAction extends OnCreateAction {

	private int index;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		index=ints.next();
		squareIndexOffset = index;
	}
	@Override
	public void act(OnCreateSquare square) {
	}
	@Override
	public void saveTo(List<Object> saveTo){
	}
	@Override
	public int getIndex() {
		return 8;
	}
	public OnCreateAction create(){
		return new SquareIndexOffsetOnCreateAction();
	}
}
