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
			subject.safeSquare(target);
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
	public static final SquareAction activate = new SquareAction(true){
		@Override
		public void act(Hero subject) {
			//System.out.println(target);
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
		@Override
		public void saveTo(List<Object> toSave){
			super.saveTo(toSave);
			toSave.add(Hub.map.getSquares().indexOf(target));
		}
	};
	public static final SquareAction deactivate = new SquareAction(true){
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
		@Override
		public void saveTo(List<Object> toSave){
			super.saveTo(toSave);
			toSave.add(Hub.map.getSquares().indexOf(target));
		}
	};
	public static final SquareAction move = new SquareAction(false){
		@Override
		public void act(Hero subject) {
			float x = target.getX();
			float y = target.getY();
			subject.push(target);
			if(Hub.map.isWithinWall(target)||subject.getPartner().isWithin(target)){
				target.setX(x);
				target.setY(y);
				subject.backup(target);
			}
		}
		@Override
		public int numberOfTargets(){
			return 0;
		}
		@Override
		public int getIndex() {
			return 5;
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
	public boolean requiresComplete(){
		return requireComplete;
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
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
		if(i==-1||i>=actions.size()){
			return null;
		}
		else {
			return actions.get(i);
		}
	}
};