package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import game.Action;
import game.Hero;

public abstract class SquareAction implements Action<Hero> {

	public static List<SquareAction> actions = new ArrayList<SquareAction>();
	public static List<String> actionNames = new ArrayList<String>();

	public static final SquareAction safe = new SquareAction(){
		@Override
		public void act(Hero subject) {
		}
	};
	public static final SquareAction impassible = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.backup(target);
		}
	};
	public static final SquareAction hazard = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.endGame();
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
	};

	protected Square target;
	public void setTarget(Square target){
		this.target = target;
	}
	public int numberOfTargets(){
		return 0;
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
};