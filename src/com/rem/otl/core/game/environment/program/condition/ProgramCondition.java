package com.rem.otl.core.game.environment.program.condition;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rem.otl.core.game.environment.Saveable;
import com.rem.otl.core.game.environment.program.DataHolder;
import com.rem.otl.core.game.environment.program.ProgramState;
import com.rem.otl.core.game.environment.program.Statable;
import com.rem.otl.core.game.environment.program.action.ProgramAction;
import com.rem.otl.core.game.environment.update.UpdateAction;

public abstract class ProgramCondition<SubjectType extends Object> extends DataHolder implements Condition<SubjectType>, Saveable {
	
	private static List<ProgramCondition> conditions = new ArrayList<ProgramCondition>();
	public static List<String> conditionNames = new ArrayList<String>();
	public static final FreeProgramCondition free = new FreeProgramCondition();
	private ProgramState state;
	private ProgramCondition next;
	private int opperand = -1;
	
	@Override
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		super.saveTo(saveTo);
		if(next==null){
			saveTo.add(-1);
		}
		else {
			saveTo.add(opperand);
			next.saveTo(saveTo);
		}
	}

	@Override
	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		super.loadFrom(ints, floats);
		this.opperand = ints.next();
		if(opperand>-1){
			next = ProgramCondition.getCondition(ints.next()).create();
			next.setState(state);
			next.loadFrom(ints, floats);
		}
	}
	public ProgramCondition getNext() {
		return next;
	}

	public void setNext(ProgramCondition nextCondition) {
		this.next = nextCondition;
	}
	@Override
	public int saveType() {
		return 8;
	}
	@Override
	public ProgramState getState() {
		return state;
	}
	@Override
	public void setState(ProgramState state) {
		this.state = state;
	}
	public String getName() {
		return conditionNames.get(this.getIndex());
	}
	public abstract int targetType();
	protected abstract int getIndex();
	public abstract ProgramCondition create();

	static {
		try {
			
			Map<Integer,ProgramCondition> osas = new HashMap<Integer,ProgramCondition>();
			Map<Integer,String> names = new HashMap<Integer,String>();
			
			for(Field field:ProgramCondition.class.getFields()){
				Object obj = field.get(ProgramCondition.class);
				if(obj instanceof ProgramCondition){
					//System.out.println(field.getName());
					ProgramCondition osa = (ProgramCondition) obj;
					osas.put(osa.getIndex(),osa);
					names.put(osa.getIndex(),field.getName().replace('_', ' '));
				}
			}
			for(int i=0;i<osas.size();++i){
				conditions.add(osas.get(i));
				conditionNames.add(names.get(i));
			}
		}
		catch (IllegalArgumentException e){			
			e.printStackTrace();
		}
		catch  (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static ProgramCondition getCondition(Integer i) {
		if(i==-1||i>=conditions.size()){
			return null;
		}
		else {
			return conditions.get(i);
		}
	}

	public static ProgramCondition getCondition(String subject) {
		int index = conditionNames.indexOf(subject);
		if(index==-1){
			return null;
		}
		else {
			return conditions.get(index);
		}
	}
	
}
