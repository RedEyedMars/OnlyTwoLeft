package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import game.environment.Square;

public class CopyOnCreateAction extends OnCreateAction {
	private int indexOfList;
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		indexOfList=ints.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		Square toCopy = (Square) square.getData().get(indexOfList);
		square.add(Square.copy(toCopy));
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(indexOfList);
	}
	@Override
	public int getIndex() {
		return 9;
	}
	public OnCreateAction create(){
		return new CopyOnCreateAction();
	}
}
