package game.environment.oncreate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.environment.Square;
import main.Hub;

public class CreateSquaresOnCreateAction extends OnCreateAction {

	private List<Square> list = new ArrayList<Square>();		
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		int size = ints.next();
		for(int i=0;i<size;++i){
			int index = ints.next();
			if(index>=0){
				list.add(Hub.map.getTemplateSquares().get(index+squareIndexOffset));				
			}
		}
	}
	@Override
	public void act(OnCreateSquare square) {
		square.add(list);
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(list.size());
		for(int i=0;i<list.size();++i){
			saveTo.add(Hub.map.getTemplateSquares().indexOf(list.get(i)));
		}
	}
	@Override
	public int getIndex() {
		return 3;
	}
	public OnCreateAction create(){
		return new CreateSquaresOnCreateAction();
	}
}
