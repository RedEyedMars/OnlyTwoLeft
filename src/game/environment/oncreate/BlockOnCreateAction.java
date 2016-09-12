package game.environment.oncreate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class BlockOnCreateAction extends OnCreateAction implements List<OnCreateAction>{
	
	protected List<OnCreateAction> actions = new ArrayList<OnCreateAction>();
	
	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		this.ints.clear();
		this.floats.clear();
		this.actions.clear();
		super.loadFrom(ints, floats);
		int numberOfActions = this.ints.get(0);
		for(int i=0;i<numberOfActions;++i){
			int actionIndex = ints.next();
			OnCreateAction action = OnCreateAction.actions.get(actionIndex).create();
			action.loadFrom(ints, floats);
			this.actions.add(action);
		}
	}
	@Override
	public void saveArgs(List<Object> saveTo){
		saveTo.add(actions.size());
		for(OnCreateAction action:actions){
			action.saveTo(saveTo);
		}
	}

	@Override
	public boolean isBlock(){
		return true;
	}
	@Override
	public boolean add(OnCreateAction e) {
		return actions.add(e);
	}

	@Override
	public void add(int index, OnCreateAction element) {
		actions.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends OnCreateAction> c) {
		return actions.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends OnCreateAction> c) {
		return actions.addAll(index, c);
	}

	@Override
	public void clear() {
		actions.clear();
	}

	@Override
	public boolean contains(Object o) {
		return actions.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return actions.containsAll(c);
	}

	@Override
	public OnCreateAction get(int index) {
		return actions.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return actions.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return actions.isEmpty();
	}

	@Override
	public Iterator<OnCreateAction> iterator() {
		return actions.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return actions.lastIndexOf(o);
	}

	@Override
	public ListIterator<OnCreateAction> listIterator() {
		return actions.listIterator();
	}

	@Override
	public ListIterator<OnCreateAction> listIterator(int index) {
		return actions.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return actions.remove(o);
	}

	@Override
	public OnCreateAction remove(int index) {
		return actions.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return actions.remove(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return actions.retainAll(c);
	}

	@Override
	public OnCreateAction set(int index, OnCreateAction element) {
		return actions.set(index, element);
	}

	@Override
	public int size() {
		return actions.size();
	}

	@Override
	public List<OnCreateAction> subList(int fromIndex, int toIndex) {
		return actions.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return actions.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return actions.toArray(a);
	}

}
