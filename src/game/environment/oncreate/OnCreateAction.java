package game.environment.oncreate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import game.Action;
import game.Hero;
import game.environment.Square;
import game.environment.SquareAction;
import main.Hub;
import main.Main;


public abstract class OnCreateAction implements SquareAction<OnCreateSquare,Object>{
	public static Map<String,OnCreateAction> actionMap = new HashMap<String,OnCreateAction>();
	public static List<OnCreateAction> actions = new ArrayList<OnCreateAction>();
	public static int squareIndexOffset = 0;

	public static final OnCreateAction section = new SectionOnCreateAction();
	public static final OnCreateAction create_list = new CreateListOnCreateAction();
	public static final OnCreateAction create_square = new CreateSquareOnCreateAction();
	public static final OnCreateAction create_squares = new CreateSquaresOnCreateAction();
	public static final OnCreateAction put = new PutOnCreateAction();
	public static final OnCreateAction get = new GetOnCreateAction();
	public static final OnCreateAction get_random = new GetRandomOnCreateAction();
	public static final OnCreateAction display_list = new DisplayListOnCreateAction();
	public static final OnCreateAction square_index_offset = new SquareIndexOffsetOnCreateAction();
	public static final OnCreateAction copy = new CopyOnCreateAction();
	public static final OnCreateAction _for = new ForOnCreateAction();
	public static final OnCreateAction translate_x = new TranslateXOnCreateAction();
	public static final OnCreateAction translate_y = new TranslateYOnCreateAction();
	public static final OnCreateAction adjust_startPERCENT = new AdjustLimiterStartPercentOnCreateAction();

	protected List<Float> floats = new ArrayList<Float>();
	protected List<Integer> ints = new ArrayList<Integer>();
	@Override
	public int targetType() {
		return 0;
	}
	@Override
	public void setTarget(Object obj) {			
	}
	public int numberOfFloats(){
		return 0;
	}
	public int numberOfInts(){
		return 0;
	}
	public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
		for(int i=0;i<numberOfFloats();++i){
			this.floats.add(floats.next());
		}
		for(int i=0;i<numberOfInts();++i){
			this.ints.add(ints.next());
		}
	}

	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		saveArgs(saveTo);
	}

	public abstract OnCreateAction create();
	public boolean isBlock(){
		return false;
	}

	protected void saveArgs(List<Object> saveTo){
		for(int i=0;i<numberOfInts();++i){
			saveTo.add(ints.get(i));
		}
		for(int i=0;i<numberOfFloats();++i){
			saveTo.add(floats.get(i));
		}
	}

	static {
		try {
			for(Field field:OnCreateAction.class.getFields()){
				Object obj = field.get(OnCreateAction.class);
				if(obj instanceof OnCreateAction){
					//System.out.println(field.getName());
					String name = field.getName().replace('_', ' ');
					name = name.replace("PERCENT", "%");
					while(name.startsWith(" ")){
						name = name.substring(1);
					}
					actionMap.put(name,(OnCreateAction) obj);
					actions.add((OnCreateAction) obj);
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static OnCreateAction getAction(Integer i) {
		if(i==-1){
			return null;
		}
		else {
			return actions.get(i);
		}
	}
}
