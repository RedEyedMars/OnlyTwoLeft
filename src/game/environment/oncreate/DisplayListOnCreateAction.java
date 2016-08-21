package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import game.environment.Square;
import main.Hub;

public class DisplayListOnCreateAction extends OnCreateAction {

	private int indexOfList;
	@Override
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		indexOfList=ints.next();
	}
	@Override
	public void act(OnCreateSquare square) {
		List<Object> list = (List<Object>) square.getData().get(indexOfList);
		float dx = 10000f;
		float dy = 10000f;
		for(Object sqr:list){
			if(((Square)sqr).getX()<dx){
				dx=((Square)sqr).getX();
			}
			if(((Square)sqr).getY()<dy){
				dy=((Square)sqr).getY();
			}
		}
		dx=square.getX()-dx;
		dy=square.getY()-dy;
		for(Object sqr:list){
			Square s = Square.copy((Square)sqr);
			s.setX(s.getX()+dx);
			s.setY(s.getY()+dy);
			square.addChild(s);
			if(Hub.map!=null){
				Hub.map.displaySquare(s);
			}
		}
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		saveTo.add(indexOfList);
	}
	@Override
	public int getIndex() {
		return 7;
	}
	public OnCreateAction create(){
		return new DisplayListOnCreateAction();
	}

}
