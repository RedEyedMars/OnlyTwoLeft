package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import game.Action;
import game.Hero;
import main.Hub;

public abstract class SquareAction implements Action<Hero> {

	public static List<SquareAction> actions = new ArrayList<SquareAction>();
	public static List<String> actionNames = new ArrayList<String>();

	public static final SquareAction safe = new SquareAction(true){
		@Override
		public void act(Hero subject) {
		}
		@Override
		public int getIndex() {
			return 0;
		}
	};
	public static final SquareAction impassible = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.backup((FunctionalSquare) target);
		}

		@Override
		public int getIndex() {
			return 1;
		}
	};
	public static final SquareAction hazard = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.endGame();
		}
		@Override
		public int getIndex() {
			return 2;
		}
	};
	public static final SquareAction activate = new SquareAction(){
		@Override
		public void act(Hero subject) {
			if(target instanceof UpdatableSquare){
				((UpdatableSquare)target).activate();
			}
		}
		@Override
		public int numberOfTargets(){
			return 1;
		}
		@Override
		public int getIndex() {
			return 3;
		}
	};
	public static final SquareAction deactivate = new SquareAction(){
		@Override
		public void act(Hero subject) {
			if(target instanceof UpdatableSquare){
				((UpdatableSquare)target).deactivate();
			}
		}
		@Override
		public int numberOfTargets(){
			return 1;
		}
		@Override
		public int getIndex() {
			return 4;
		}
	};

	private boolean requireComplete;
	public SquareAction(){
		this(false);
	}
	public SquareAction(boolean requireComplete){
		this.requireComplete = requireComplete;
	}
	public boolean isWithin(Hero hero, FunctionalSquare e){
		if(requireComplete){
			return hero.isCompletelyWithin(e);
		}
		else {
			return hero.isWithin(e);
		}
	}
	protected Square target;
	public void setTarget(Square target){
		this.target = target;
	}
	public int numberOfTargets(){
		return 0;
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		if(numberOfTargets()==1){
			saveTo.add(Hub.map.getSquares().indexOf(target));
		}
	}
	static {
		try {
			for(Field field:SquareAction.class.getFields()){
				Object obj = field.get(SquareAction.class);
				if(obj instanceof SquareAction){
					//System.out.println(field.getName());
					actions.add((SquareAction) obj);
					actionNames.add(field.getName());
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static SquareAction getAction(Integer i) {
		if(i==-1){
			return null;
		}
		else {
			return actions.get(i);
		}
	}
};