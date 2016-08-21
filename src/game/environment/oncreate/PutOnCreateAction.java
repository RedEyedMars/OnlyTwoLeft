package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

public class PutOnCreateAction extends OnCreateAction {

	private int indexOfList;
	@Override
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		indexOfList=ints.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		List<Object> list = (List<Object>) square.getData().get(indexOfList);
		list.add(square.useLast());
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(indexOfList);
	}
	@Override
	public int getIndex() {
		return 4;
	}
	public OnCreateAction create(){
		return new PutOnCreateAction();
	}
}
