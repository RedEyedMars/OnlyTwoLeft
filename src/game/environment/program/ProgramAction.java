package game.environment.program;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import game.environment.Saveable;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.oncreate.OnCreateAction;
import game.environment.onstep.OnStepAction;
import game.environment.program.condition.ProgramCondition;
import game.environment.update.UpdateAction;

public abstract class ProgramAction <SubjectType extends Object> extends DataHolder implements SquareAction<SubjectType, ProgramState>{

	private static List<ProgramAction> actions = new ArrayList<ProgramAction>();
	public static List<String> actionNames = new ArrayList<String>();
	public static final BaseProgramAction base = new BaseProgramAction();
	public static final SetColourProgramAction set_colour = new SetColourProgramAction();
	public static final SetUpdateActionProgramAction set_update_action = new SetUpdateActionProgramAction();
	
	private ProgramState state;
	
	@Override
	public void saveTo(List<Object> saveTo) {
		saveTo.add(getIndex());
		super.saveTo(saveTo);
	}
	@Override
	public int saveType() {
		return 7;
	}
	@Override
	public void setTarget(ProgramState target) {
		this.state = target;
	}
	@Override
	public ProgramState getState() {
		return state;
	}
	@Override
	public void setState(ProgramState state) {
		this.state = state;
	}
	public abstract ProgramAction create();
	static {
		try {
			for(Field field:ProgramAction.class.getFields()){
				Object obj = field.get(ProgramAction.class);
				if(obj instanceof ProgramAction){
					//System.out.println(field.getName());
					actions.add((ProgramAction) obj);
					actionNames.add(field.getName().replace('_', ' '));
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static ProgramAction getAction(Integer i) {
		//System.out.println(i);
		if(i==-1||i>=actions.size()){
			return null;
		}
		else {
			return actions.get(i);
		}
	}
}
