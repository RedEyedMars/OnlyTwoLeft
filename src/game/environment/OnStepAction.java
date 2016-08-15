package game.environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import duo.client.Client;
import duo.messages.EndGameMessage;
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
		@Override
		public int getIndex() {
			return 0;
		}
	};
	public static final OnStepAction impassible = new OnStepAction(){
		@Override
		public void act(Hero subject) {
			if(target instanceof UpdatableSquare){
				UpdatableSquare square = ((UpdatableSquare)target);
				for(UpdateAction updateAction:square.getAction()){
					if(updateAction.getIndex()==0||updateAction.getIndex()==1){
						if(subject.getXAcceleration()==0){
							subject.setXVelocity(subject.getXVelocity()+updateAction.getFloat(0)/0.9f);
						}
						if(subject.getYAcceleration()==0){
							if(updateAction.getFloat(1)<0){
								subject.setYVelocity(subject.getYVelocity()+updateAction.getFloat(1)/0.9f);
							}
						}
					}
				}
			}
		}
		public boolean isSafe(){
			return false;
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
		public boolean isSafe(){
			return false;
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
		public void setTarget(Square target){
			if(this.target==null){
				this.target = target;
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
	public static final OnStepAction deactivate = new OnStepAction(){
		@Override
		public void act(Hero subject) {
			if(target instanceof UpdatableSquare){
				((UpdatableSquare)target).deactivate();
			}
		}
		@Override
		public void setTarget(Square target){
			if(this.target==null){
				this.target = target;
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
	public static final OnStepAction move = new OnStepAction(){
		@Override
		public boolean resolve(Hero subject){
			return subject.push((OnStepSquare)target);
		}
		@Override
		public void act(Hero subject) {
		}
		@Override
		public boolean isSafe(){
			return false;
		}
		@Override
		public int getIndex() {
			return 5;
		}
	};
	public static final OnStepAction colour_change_block = new OnStepAction(){
		@Override
		public void act(Hero subject) {
			//subject.colourChange(target.textureIndex());
		}
		@Override
		public boolean isSafe(){
			return false;
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
		return true;
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
			return null;
		}
		else {
			return actions.get(i);
		}
	}
};