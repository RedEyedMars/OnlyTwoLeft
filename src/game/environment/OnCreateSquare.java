package game.environment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.Action;
import main.Hub;

public class OnCreateSquare extends Square{

	private OnCreateAction action;
	private List<Object> data = new ArrayList<Object>();

	public OnCreateSquare(int blackColour, int whiteColour, int bufferSize,Iterator<Integer> ints, Iterator<Float> floats) {
		super(blackColour,whiteColour, bufferSize,ints, floats);
		actionType = 6;
		this.action = OnCreateAction.section.create();
		this.action.setArgs(ints, floats);
	}
	
	public void act(){
		this.action.act(this);
	}

	public List<Object> getData(){
		return data;
	}
	public Object get(int i){
		return data.get(i);
	}
	public void add(Object obj){
		data.add(obj);
	}
	public Object useLast() {
		return data.remove(data.size()-1);
	}
	@Override
	public List<SquareAction> getActions() {
		List<SquareAction> list = super.getActions();
		list.add(action);
		return list;
	}
}
