package game.environment.program;

import java.util.ArrayList;
import java.util.List;

import game.environment.Saveable;

public class Variable {
	public static final int NULL = -3;
	public static final int BOOLEAN = -1;
	public static final int INTEGER = 0;
	public static final int FLOAT = 1;
	public static final int STRING = -2;
	
	private int type = NULL;
	private Object value = null;
	private String name;
	private List<VariableListener> listeners = new ArrayList<VariableListener>();
	
	public Variable(String name, Object value) {
		setValue(value);
		this.name = name;
	}
	
	public void setValue(Object obj){
		if(obj == null){
			type = NULL;
		}
		else if(obj instanceof Integer){
			type = INTEGER;
		}
		else if(obj instanceof Float){
			type = FLOAT;
		}
		else if(obj instanceof Boolean){
			type = BOOLEAN;
		}
		else if(obj instanceof String){
			type = STRING;
		}
		else if(obj instanceof Saveable){
			type = ((Saveable)obj).saveType();
		}
		this.value = obj;	
		for(VariableListener listener:listeners ){
			listener.onVariableChange(name,value);
		}
	}

	public Object getValue() {
		return value;
	}
	public boolean is(int type) {
		return this.type == type;
	}

	public void saveTo(List<Object> saveTo) {

		saveTo.add(type);
		if(type==STRING){
			char[] charArray = ((String)value).toCharArray();
			saveTo.add(charArray.length);
			for(char c:charArray){
				saveTo.add(new Integer((int)c));
			}
		}
		else if(type==BOOLEAN){
			saveTo.add(((Boolean)value)?1:0);
		}
		else if(type==INTEGER){
			saveTo.add(value);
		}
		else if(type==FLOAT) {
			saveTo.add(value);
		}
		else {
			//	System.out.println("save"+arg);
			((Saveable) value).saveTo(saveTo);
		}
	}

	public void addListener(VariableListener listener) {
		this.listeners.add(listener);
	}

}
