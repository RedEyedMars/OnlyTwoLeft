package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import game.Hero;
import game.Action;

public abstract class UpdateAction implements Action<Double>{
	public static List<UpdateAction> actions = new ArrayList<UpdateAction>();
	public static List<String> actionNames = new ArrayList<String>();

	
	public static final UpdateAction grow = new UpdateAction(){
		@Override
		public void act(Double seconds) {
		}
		@Override
		public int numberOfFloats(){
			return 2;
		}
	};

	private List<Float> data = new ArrayList<Float>(numberOfFloats());
	public int numberOfFloats(){
		return 0;
	}
	public void setFloats(Iterator<Float> floats){
		for(int i=0;i<numberOfFloats();++i){
			data.add(floats.next());
		}
	}
	public void saveTo(List<Object> saveTo){
		for(Float flt:data){
			saveTo.add(flt);			
		}
	}
	static {
		try {
			for(Field field:UpdateAction.class.getFields()){
				Object obj = field.get(UpdateAction.class);
				if(obj instanceof UpdateAction){
					//System.out.println(field.getName());
					actions.add((UpdateAction) obj);
					actionNames.add(field.getName());
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
