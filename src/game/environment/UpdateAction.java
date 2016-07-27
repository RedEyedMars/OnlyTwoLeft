package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import editor.Button;
import editor.ButtonAction;
import editor.Editor;
import game.Hero;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import game.Action;

public abstract class UpdateAction implements SquareAction<Double>{
	public static List<UpdateAction> actions = new ArrayList<UpdateAction>();
	public static List<String> actionNames = new ArrayList<String>();

	public void setSelf(UpdatableSquare self){
		this.self = self;
	}
	protected UpdatableSquare self;

	public static final UpdateAction grow = new UpdateAction(){
		@Override
		public void act(Double seconds) {
			self.adjust((float) (self.getWidth()+data.get(0)*seconds), (float) (self.getHeight()+data.get(1)*seconds));
		}
		@Override
		public int numberOfFloats(){
			return 2;
		}
		@Override
		public int getIndex() {
			return 0;
		}
	};

	protected ArrayList<Float> data = new ArrayList<Float>(){
		@Override
		public boolean add(Float obj){
			if(this.size()>=numberOfFloats()){
				this.clear();
			}
			return super.add(obj);
		}
	};
	public int numberOfFloats(){
		return 0;
	}
	public void setFloats(Iterator<Float> floats){
		for(int i=0;i<numberOfFloats();++i){
			data.add(floats.next());
		}
	}
	public float getFloat(int i){
		return data.get(i);
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		for(Float flt:data){
			saveTo.add(flt);			
		}
	}
	@Override
	public int numberOfTargets() {
		return 0;
	}
	@Override
	public void setTarget(Square square) {
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
	public static UpdateAction getAction(Integer i) {
		if(i==-1||i>=actions.size()){
			return null;
		}
		else {
			return actions.get(i);
		}
	}
	public void addFloats(float x, float y) {
		data.add(x);				
		if(data.size()<numberOfFloats()){
			data.add(y);
		}
	}
	public UpdateAction create() {
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		};
		return null;
	}
}
