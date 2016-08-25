package game.environment.update;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CombinedUpdateActions extends UpdateAction{
	List<UpdateAction> actions = new ArrayList<UpdateAction>();
	@Override
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		this.defaultState=true;
		int size = ints.next();
		for(int i=0;i<size;++i){
			UpdateAction action = UpdateAction.getAction(ints.next()).create();
			action.setArgs(ints, floats);
			action.setTarget(self);
			actions.add(action);
		}
	}
	@Override
	public void saveTo(List<Object> toSave){
		toSave.add(getIndex());
		toSave.add(actions.size());
		for(UpdateAction action:actions){
			action.saveTo(toSave);
		}
	}
	@Override
	public int getIndex() {
		return -2;
	}

	@Override
	public void act(Double subject) {
		for(UpdateAction action:actions){
			action.act(subject);
		}
	}
	@Override
	public void setTarget(UpdatableSquare target){
		super.setTarget(target);
		for(UpdateAction action:actions){
			action.setTarget(target);
		}
	}
	@Override
	public void undo(){
		for(UpdateAction action:actions){
			action.undo();
		}
	}
	@Override
	public void onActivate(){
		for(UpdateAction action:actions){
			action.onActivate();
		}		
	}
	@Override
	public void onDeactivate(){
		for(UpdateAction action:actions){
			action.onDeactivate();
		}		
	}

	@Override
	public int targetType(){
		return actions.size();
	}	

	public Iterator<UpdateAction> iterator(){
		return new Iterator<UpdateAction>(){
			private int index = 0;
			private Iterator<UpdateAction> currentIterator=null;
			{
				if(index<actions.size()){
					currentIterator = actions.get(index++).iterator();
				}
			}
			@Override
			public boolean hasNext() {
				if(currentIterator==null)return false;
				return currentIterator.hasNext()||index<actions.size();
			}

			@Override
			public UpdateAction next() {
				while(!currentIterator.hasNext()&&index<actions.size()){
					currentIterator=actions.get(index++).iterator();
				}
				if(currentIterator.hasNext()){
					return currentIterator.next();
				}
				else {
					return null;
				}
			}};
	}
	@Override
	public UpdateAction create(){
		return new CombinedUpdateActions();
	}
}
