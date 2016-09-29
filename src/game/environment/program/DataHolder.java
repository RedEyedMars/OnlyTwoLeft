package game.environment.program;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import editor.program.Settable;
import game.environment.Saveable;
import game.environment.Square;
import game.environment.oncreate.OnCreateAction;
import game.environment.onstep.OnStepAction;
import game.environment.program.condition.ProgramCondition;
import game.environment.update.NullUpdateAction;
import game.environment.update.UpdateAction;

public abstract class DataHolder implements Statable, Settable{
	private LinkedHashMap<String,Object> data = new LinkedHashMap<String,Object>();
	private String[] valueNames;
	private String[] intNames;

	private Integer[] valueIds;
	private Integer[] intIds;
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
			else if(arg instanceof Integer){
				saveTo.add(0);
				saveTo.add(arg);
			}
			else if(arg instanceof Float) {
				saveTo.add(1);
				saveTo.add(arg);
			}
			else if(arg instanceof Saveable){
				//	System.out.println("save"+arg);
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
		case 0: return (Integer)ints.next();
		case 1: return (Float)floats.next();
		case 2: return Square.create(ints, floats);
		case 3: {
			OnStepAction onStepAction = OnStepAction.getAction(ints.next()).create();
			if(onStepAction.targetType()==2){
				onStepAction.setTarget(ints.next());
			}
			return onStepAction;
		}
		case 4: {
			UpdateAction updateAction = UpdateAction.getAction(ints.next());
			if(updateAction==null){
				updateAction = new NullUpdateAction();
			}
			else {
				updateAction = updateAction.create();
			}
			updateAction.loadFrom(ints, floats);
			//	System.out.println("loadDatum updateAction"+updateAction.getValue(UpdateAction.X)+","+updateAction.getValue(UpdateAction.Y));
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
	@Override
	public String[] copiableIntNames() {
		if(intNames==null){
			List<String> names = new ArrayList<String>();
			for(String key:data.keySet()){
				if(data.get(key) instanceof Integer||(data.get(key) instanceof Boolean)){
					names.add(key);
				}
			}
			intNames = names.toArray(new String[0]);
		}
		return intNames;
	}
	@Override
	public String[] copiableValueNames() {
		if(valueNames==null){
			List<String> names = new ArrayList<String>();
			for(String key:data.keySet()){
				if(data.get(key) instanceof Float){
					names.add(key);
				}
			}
			valueNames = names.toArray(new String[0]);
		}
		return valueNames;
	}

	@Override
	public Integer[] copiableIntIds() {
		if(intIds==null){
			int i = 0;
			List<Integer> ids = new ArrayList<Integer>();
			for(String key:data.keySet()){
				if((data.get(key) instanceof Integer)||(data.get(key) instanceof Boolean)){
					ids.add(i++);
				}
			}
			intIds = ids.toArray(new Integer[0]);
		}
		return intIds;
	}
	@Override
	public Integer[] copiableValueIds() {
		if(valueIds==null){
			int i = 0;
			List<Integer> ids = new ArrayList<Integer>();
			for(String key:data.keySet()){
				if(data.get(key) instanceof Float){
					ids.add(i++);
				}
			}
			valueIds = ids.toArray(new Integer[0]);
		}
		return valueIds;
	}
	@Override
	public void setValue(int index, float value) {
		setData(copiableValueNames()[index],value);
	}
	@Override
	public void setValue(int index, int value) {
		if(getData(copiableIntNames()[index]) instanceof Integer){
			setData(copiableIntNames()[index],value);
			return;
		}
		else if(getData(copiableIntNames()[index]) instanceof Boolean){
			setData(copiableIntNames()[index],value==copiableIntTextureRanges()[index*2]);
			return;
		}
		throw new RuntimeException(copiableIntNames()[index]+"("+index+") is "+getData(copiableIntNames()[index])+" which is neither an Integer nor a Boolean, it's a "+getData(copiableIntNames()[index]).getClass());
	}
	@Override
	public float getValue(int index) {
		return (float) getData(copiableValueNames()[index]);
	}
	@Override
	public int getInt(int index) {
		if(getData(copiableIntNames()[index]) instanceof Integer){
			return (int)getData(copiableIntNames()[index]);
		}
		else if(getData(copiableIntNames()[index]) instanceof Boolean){
			return ((Boolean)getData(copiableIntNames()[index]))?1:0;
		}
		throw new RuntimeException(copiableIntNames()[index]+"("+index+") is "+getData(copiableIntNames()[index])+" which is neither an Integer nor a Boolean");
		
	}
}
