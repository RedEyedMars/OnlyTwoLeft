package game.environment.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import game.environment.Saveable;
import game.environment.Square;
import game.environment.oncreate.OnCreateAction;
import game.environment.onstep.OnStepAction;
import game.environment.program.condition.ProgramCondition;
import game.environment.update.UpdateAction;

public abstract class DataHolder implements Statable{
private LinkedHashMap<String,Object> data = new LinkedHashMap<String,Object>();	
	public Object getData(String dataName){
		return data.get(dataName);
	}
	public List<Object> getData() {
		List<Object> d = new ArrayList<Object>();
		for(String key:data.keySet()){
			d.add(data.get(key));
		}
		return d;
	}

	public Collection<String> getDataKeys() {
		return data.keySet();
	}
	public void setData(String dataName, Object dataValue){
		data.put(dataName, dataValue);
	}
	public void saveTo(List<Object> saveTo) {
		for(String key:data.keySet()){
			Object arg = data.get(key);
			if(arg instanceof Boolean){
				saveTo.add(-1);
				saveTo.add(((Boolean)arg)?1:0);
			}
			else if((arg instanceof Integer)||(arg instanceof Boolean)){
				saveTo.add(0);
				saveTo.add(arg);
			}
			else if(arg instanceof Float) {
				saveTo.add(1);
				saveTo.add(arg);
			}
			else if(arg instanceof Saveable){
				saveTo.add(((Saveable) arg).saveType());
				((Saveable) arg).saveTo(saveTo);
			}
		}
	}

	public void loadFrom(Iterator<Integer> ints, Iterator<Float> floats){
		ProgramState state = this.getState();
		for(String key:data.keySet()){
			int type = ints.next();
			Object datum = loadDatum(type,state,ints,floats);
			data.put(key, datum);
		}
	}

	private Object loadDatum(int type,ProgramState state, Iterator<Integer> ints, Iterator<Float> floats) {
		switch(type){
		case -1: return new Boolean(ints.next()==1);
		case 0: return ints.next();
		case 1: return floats.next();
		case 2: return Square.create(ints, floats);
		case 3: {
			OnStepAction onStepAction = OnStepAction.getAction(ints.next()).create();
			if(onStepAction.targetType()==2){
				onStepAction.setTarget(ints.next());
			}
			return onStepAction;
		}
		case 4: {
			UpdateAction updateAction = UpdateAction.getAction(ints.next()).create();
			updateAction.loadFrom(ints, floats);
			return updateAction;
		}
		case 5:{
			OnCreateAction onCreateAction = OnCreateAction.getAction(ints.next()).create();
			onCreateAction.loadFrom(ints, floats);
			return onCreateAction;
		}
		case 6:{
			ProgramState advancedAction = new ProgramState();
			advancedAction.loadFrom(ints, floats);
			return advancedAction;
		}
		case 7:{
			ProgramAction programAction = ProgramAction.getAction(ints.next()).create();
			programAction.setState(state);
			programAction.loadFrom(ints, floats);
			return programAction;
		}
		case 8:{
			ProgramCondition programCondition = ProgramCondition.getCondition(ints.next()).create();
			programCondition.setState(state);
			programCondition.loadFrom(ints, floats);
			return programCondition;
		}
		}
		return null;
	}
}
