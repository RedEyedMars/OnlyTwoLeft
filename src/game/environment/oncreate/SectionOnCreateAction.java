package game.environment.oncreate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SectionOnCreateAction extends OnCreateAction{
	private int numberOfActions = 0;
	private List<OnCreateAction> actions = new ArrayList<OnCreateAction>();
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		squareIndexOffset=0;
		this.ints.clear();
		this.actions.clear();
		super.setArgs(ints, floats);
		numberOfActions = this.ints.get(0);
		for(int i=0;i<numberOfActions;++i){
			int actionIndex = ints.next();
			OnCreateAction action = OnCreateAction.actions.get(actionIndex).create();
			action.setArgs(ints, floats);
			this.actions.add(action);
		}
	}
	@Override
	public void act(OnCreateSquare square) {
		square.getData().clear();
		for(OnCreateAction action:actions){
			action.act(square);
		}
	}
	@Override
	public void saveTo(List<Object> saveTo){
		saveTo.add(numberOfActions);
		for(OnCreateAction action:actions){
			action.saveTo(saveTo);
		}
	}
	@Override
	public int numberOfInts(){
		return 1;
	}
	@Override
	public int getIndex() {
		return 0;
	}
	
	public OnCreateAction create(){
		return new SectionOnCreateAction();
	}
}
