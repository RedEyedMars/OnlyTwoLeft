package game.environment.onstep;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import game.Hero;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.update.UpdatableSquare;

public abstract class OnStepAction<TargetType extends Object> implements SquareAction<Hero,TargetType> {

	public static List<OnStepAction> actions = new ArrayList<OnStepAction>();

	public static final OnStepAction<Square> safe = new SafeOnStepAction();
	public static final OnStepAction<Square> wall = new WallOnStepAction();
	public static final OnStepAction<Square> hazard = new HazardOnStepAction();
	public static final OnStepAction<UpdatableSquare> activate = new ActivateOnStepAction();
	public static final OnStepAction<UpdatableSquare> deactivate = new DeactivateOnStepAction();
	public static final OnStepAction<OnStepSquare> move = new MoveOnStepAction();
	public static final OnStepAction<Integer> win_stage = new WinStageOnStepAction();
	public static final OnStepAction<Object> null_action = new NullOnStepAction();

	protected TargetType target;
	public void setTarget(TargetType target){
		this.target = target;
	}
	public int targetType(){
		return 0;
	}
	public boolean isPassible(){
		return true;
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
	}
	public boolean resolve(Hero subject){
		return false;
	}
	public int saveType(){
		return 3;
	}
	public abstract OnStepAction<TargetType> create();
	static {
		try {
			for(Field field:OnStepAction.class.getFields()){
				Object obj = field.get(OnStepAction.class);
				if(obj instanceof OnStepAction){
					//System.out.println(field.getName());
					actions.add((OnStepAction) obj);
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