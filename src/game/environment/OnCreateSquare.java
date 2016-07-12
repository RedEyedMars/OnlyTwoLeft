package game.environment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.Action;

public class OnCreateSquare extends Square{

	private OnCreateAction action;
	private List<Object> data = new ArrayList<Object>();

	public OnCreateSquare(Iterator<Integer> ints, Iterator<Float> floats, OnCreateAction action) {
		super(ints, floats);
		actionType = 6;
		try {
			this.action = action.getClass().newInstance();
			this.action.setArgs(ints, floats);
			this.action.act(this);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
	@Override
	public List<Action> getActions() {
		List<Action> list = super.getActions();
		list.add(action);
		return list;
	}
}
