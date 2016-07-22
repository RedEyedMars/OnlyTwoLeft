package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import game.Action;
import game.Hero;
import main.Hub;
import main.Main;


public abstract class OnCreateAction implements Action<OnCreateSquare>{
	public static Map<String,OnCreateAction> actions = new HashMap<String,OnCreateAction>();
	public static List<OnCreateAction> actionList = new ArrayList<OnCreateAction>();

	public static final OnCreateAction section = new OnCreateAction(){
		private int numberOfActions = 0;
		private List<OnCreateAction> actions = new ArrayList<OnCreateAction>();
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			this.ints.clear();
			this.actions.clear();
			super.setArgs(ints, floats);
			numberOfActions = this.ints.get(0);
			for(int i=0;i<numberOfActions;++i){
				int actionIndex = ints.next();
				OnCreateAction action = actionList.get(actionIndex).create();
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
	};

	public static final OnCreateAction create_list = new OnCreateAction(){
		@Override
		public void act(OnCreateSquare square) {
			square.add(new ArrayList<Object>());
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
	public static final OnCreateAction create_squares = new OnCreateAction(){
		private List<Square> list = new ArrayList<Square>();		
		@Override
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			int size = ints.next();
			for(int i=0;i<size;++i){
				list.add(Square.create(ints, floats));
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
				list.get(i).saveTo(saveTo);
			}
		}
		@Override
		public int getIndex() {
			return 3;
		}
	};
	public static final OnCreateAction put = new OnCreateAction(){
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
			return 4;
		}
	};
	public static final OnCreateAction get = new OnCreateAction(){
		private int indexOfList;
		@Override
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			indexOfList=ints.next();
		}
		@Override
		public void act(OnCreateSquare square) {
			List<Object> list = (List<Object>) square.getData().get(indexOfList);
			square.add(list.remove(list.size()-1));
		}
		@Override
		protected void saveArgs(List<Object> saveTo){
			saveTo.add(indexOfList);
		}
		@Override
		public int getIndex() {
			return 5;
		}
	};
	public static final OnCreateAction get_random = new OnCreateAction(){
		private int indexOfList;
		@Override
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			indexOfList=ints.next();
		}
		@Override
		public void act(OnCreateSquare square) {
			List<Object> list = (List<Object>) square.getData().get(indexOfList);
			square.add(list.remove((int)(Main.randomizer.nextFloat()*list.size())));
		}
		@Override
		protected void saveArgs(List<Object> saveTo){
			saveTo.add(indexOfList);
		}
		@Override
		public int getIndex() {
			return 6;
		}
	};
	public static final OnCreateAction display_list = new OnCreateAction(){
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
				Square s = ((Square)sqr);
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

	public OnCreateAction create(){
		try {
			return getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
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
}
