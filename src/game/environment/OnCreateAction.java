package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import game.Action;
import game.Hero;


public abstract class OnCreateAction implements Action<OnCreateSquare>{
	public static Map<String,OnCreateAction> actions = new HashMap<String,OnCreateAction>();
	public static List<OnCreateAction> actionList = new ArrayList<OnCreateAction>();
	
	public static final OnCreateAction section = new OnCreateAction(){
		private int numberOfActions = 0;
		private List<OnCreateAction> actions = new ArrayList<OnCreateAction>();
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			super.setArgs(ints, floats);
			numberOfActions = this.ints.get(0);
			for(int i=0;i<numberOfActions;++i){
				int actionIndex = ints.next();
				try {
					OnCreateAction action = actionList.get(actionIndex).getClass().newInstance();
					action.setArgs(ints, floats);
					this.actions.add(action);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		@Override
		public void act(OnCreateSquare square) {
			for(OnCreateAction action:actions){
				action.act(square);
			}
		}
		public void saveArgs(List<Object> saveTo){
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
	};
	
	public static final OnCreateAction create_square_list = new OnCreateAction(){
		@Override
		public void act(OnCreateSquare square) {
			square.add(new ArrayList<Square>());
		}
		@Override
		public int getIndex() {
			return 1;
		}
	};
	public static final OnCreateAction create_square = new OnCreateAction(){
		private Square square;
		@Override
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			square = Square.create(ints, floats);
		}
		@Override
		public void act(OnCreateSquare square) {
			square.add(this.square);
		}
		@Override
		protected void saveArgs(List<Object> saveTo){
			square.saveTo(saveTo);
		}
		@Override
		public int getIndex() {
			return 2;
		}
	};
	public static final OnCreateAction put_into_list = new OnCreateAction(){
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
			return 3;
		}
	};
	public static final OnCreateAction display_square_list = new OnCreateAction(){
		private int indexOfList;
		@Override
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			indexOfList=ints.next();
		}
		@Override
		public void act(OnCreateSquare square) {
			List<Square> list = (List<Square>) square.getData().get(indexOfList);
			for(Square sqr:list){
				square.addChild(sqr);
			}
		}
		@Override
		protected void saveArgs(List<Object> saveTo){
			saveTo.add(indexOfList);
		}
		@Override
		public int getIndex() {
			return 4;
		}
	};
	
	protected List<Float> floats = new ArrayList<Float>();
	public int numberOfFloats(){
		return 0;
	}
	protected List<Integer> ints = new ArrayList<Integer>();
	public int numberOfInts(){
		return 0;
	}
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		for(int i=0;i<numberOfFloats();++i){
			this.floats.add(floats.next());
		}
		for(int i=0;i<numberOfInts();++i){
			this.ints.add(ints.next());
		}
	}
	
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		saveArgs(saveTo);
	}
	
	protected void saveArgs(List<Object> saveTo){
		for(int i=0;i<numberOfInts();++i){
			saveTo.add(ints.get(i));
		}
		for(int i=0;i<numberOfFloats();++i){
			saveTo.add(floats.get(i));
		}
	}
	
	static {
		try {
			for(Field field:OnCreateAction.class.getFields()){
				Object obj = field.get(OnCreateAction.class);
				if(obj instanceof OnCreateAction){
					//System.out.println(field.getName());
					actions.put(field.getName().replace('_', ' '),(OnCreateAction) obj);
					actionList.add((OnCreateAction) obj);
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static OnCreateAction getAction(Integer i) {
		if(i==-1){
			return null;
		}
		else {
			return actionList.get(i);
		}
	}
	public OnCreateAction createFromString(String toParse){
		String[] split = toParse.split(":");
		String name = split[0];
		String[] intSplit = split[1].split(",");
		String[] floatSplit = split[2].split(",");
		List<Integer> ints = new ArrayList<Integer>();
		List<Float> floats = new ArrayList<Float>();
		for(String i:intSplit){
			ints.add(Integer.parseInt(i));
		}
		for(String f:floatSplit){
			floats.add(Float.parseFloat(f));
		}
		try {
			OnCreateAction action = actions.get(name).getClass().newInstance();
			action.setArgs(ints.iterator(), floats.iterator());
			return action;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	public OnCreateAction createFromString(String name,Square square){
		final List<Integer> ints = new ArrayList<Integer>();
		final List<Float> floats = new ArrayList<Float>();
		List<Object> probe = new ArrayList<Object>(){
			@Override
			public boolean add(Object obj){
				if(obj instanceof Integer){
					return ints.add((Integer) obj);
				}
				else if(obj instanceof Float){
					return floats.add((Float) obj);
				}
				return false;
			}
		};
		square.saveTo(probe);
		try {
			OnCreateAction action = actions.get(name).getClass().newInstance();
			action.setArgs(ints.iterator(), floats.iterator());
			return action;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
