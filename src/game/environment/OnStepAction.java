package game.environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import game.Action;
import game.Hero;
import main.Hub;

public abstract class OnStepAction implements SquareAction<Hero> {

	public static List<OnStepAction> actions = new ArrayList<OnStepAction>();
	public static List<String> actionNames = new ArrayList<String>();

	public static final OnStepAction safe = new OnStepAction(){
		@Override
		public void act(Hero subject) {
		}
		public boolean isSafe(){
			return true;
		}
		@Override
		public int getIndex() {
			return 0;
		}
	};
	public static final OnStepAction impassible = new OnStepAction(){
		@Override
		public void act(Hero subject) {
		}
		@Override
		public int getIndex() {
			return 1;
		}
	};
	public static final OnStepAction hazard = new OnStepAction(){
		@Override
		public void act(Hero subject) {
			subject.endGame();
		}
		@Override
		public int getIndex() {
			return 2;
		}
	};
	public static final OnStepAction activate = new OnStepAction(){
		@Override
		public void act(Hero subject) {
			if(target instanceof UpdatableSquare){
				((UpdatableSquare)target).activate();
			}
		}
		@Override
		public boolean isSafe(){
			return true;
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
	public static final OnStepAction deactivate = new OnStepAction(){
		@Override
		public void act(Hero subject) {
			if(target instanceof UpdatableSquare){
				((UpdatableSquare)target).deactivate();
			}
		}
		@Override
		public boolean isSafe(){
			return true;
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
	public static final OnStepAction move = new OnStepAction(){
		@Override
		public boolean resolve(Hero subject){
			float x = target.getX();
			float y = target.getY();
			subject.push(target);
			if(Hub.map.isWithinWall(target,subject)||subject.getPartner().isWithin(target)){
				target.setX(x);
				target.setY(y);
				return false;
			}
			return true;
		}
		@Override
		public void act(Hero subject) {
			float x = target.getX();
			float y = target.getY();
			subject.push(target);
			if(Hub.map.isWithinWall(target,subject)||subject.getPartner().isWithin(target)){
				target.setX(x);
				target.setY(y);
			}
		}
		@Override
		public int getIndex() {
			return 5;
		}
	};
	public static final OnStepAction move_ignore_walls = new OnStepAction(){
		@Override
		public boolean resolve(Hero subject){
			float x = target.getX();
			float y = target.getY();
			subject.push(target);
			if(subject.getPartner().isWithin(target)){
				target.setX(x);
				target.setY(y);
				return false;
			}
			return true;
		}
		@Override
		public void act(Hero subject) {
			
		}
		@Override
		public int getIndex() {
			return 6;
		}
	};

	protected Square target;
	public void setTarget(Square target){
		this.target = target;
	}
	public int numberOfTargets(){
		return 0;
	}
	public boolean isSafe(){
		return false;
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
	}
	public boolean resolve(Hero subject){
		return false;
	}
	public OnStepAction create() {
		try {
			if(numberOfTargets()>0){
				return this.getClass().newInstance();
			}
			else {
				return this;
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
			e.printStackTrace();
		};
		return null;
	}
	static {
		try {
			for(Field field:OnStepAction.class.getFields()){
				Object obj = field.get(OnStepAction.class);
				if(obj instanceof OnStepAction){
					//System.out.println(field.getName());
					actions.add((OnStepAction) obj);
					actionNames.add(field.getName());
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static OnStepAction getAction(Integer i) {
		if(i==-1||i>=actions.size()){
			return actions.get(5);
		}
		else {
			return actions.get(i);
		}
	}
};