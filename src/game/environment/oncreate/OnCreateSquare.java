package game.environment.oncreate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.environment.Creatable;
import game.environment.Square;
import game.environment.SquareAction;
import gui.graphics.GraphicEntity;
import main.Hub;

public class OnCreateSquare extends Square implements Creatable{

	private OnCreateAction action;
	private List<Object> data = new ArrayList<Object>();

	public OnCreateSquare(int shapeType,int blackColour, int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super(shapeType,blackColour,whiteColour, ints, floats);
		actionType = 6;
		this.action = OnCreateAction.section.create();
		this.action.loadFrom(ints, floats);
	}

	public void create(){
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
	public Object getLast() {
		return data.get(data.size()-1);
	}
	@Override
	public List<SquareAction> getActions() {
		List<SquareAction> list = super.getActions();
		list.add(action);
		return list;
	}

}
