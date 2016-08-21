package game.environment.oncreate;

import java.util.Iterator;
import java.util.List;

import game.environment.Square;
import main.Hub;

public class CreateSquareOnCreateAction extends OnCreateAction {

	private Square square;
	@Override
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		int index = ints.next();
		if(index<Hub.map.size()){
			square = Hub.map.getTemplateSquares().get(index);
		}
		else {
			square = new Square(2,2,0.05f,0.05f);	
		}
		
	}
	@Override
	public void act(OnCreateSquare square) {
		square.add(this.square);
	}
	@Override
	protected void saveArgs(List<Object> saveTo){
		if(!Hub.map.getTemplateSquares().contains(square)){
			Hub.map.addTemplateSquare(square);
		}
		saveTo.add(Hub.map.getTemplateSquares().indexOf(square));
	}
	@Override
	public int getIndex() {
		return 2;
	}
	public OnCreateAction create(){
		return new CreateSquareOnCreateAction();
	}
}
