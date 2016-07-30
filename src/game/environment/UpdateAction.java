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
import main.Hub;
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
		public int getIndex() {
			return 0;
		}
		@Override
		public boolean defaultState(){
			return false;
		}
	};
	
	public static final UpdateAction move = new UpdateAction(){
		@Override
		public void act(Double seconds) {
			self.setX((float) (self.getX()+data.get(0)*seconds));
			self.setY((float) (self.getY()+data.get(1)*seconds));
		}
		@Override
		public int getIndex() {
			return 1;
		}
		@Override
		public boolean defaultState(){
			return true;
		}
	};
	
	public static final UpdateAction reverse = new UpdateAction(){
		@Override
		public void act(Double seconds) {
			for(UpdatableSquare square:Hub.map.getUpdateSquares()){
				if(square==self)continue;
				float x = square.getAction().getFloat(0);
				float y = square.getAction().getFloat(1);
				if((square.getX()+square.getWidth()>=self.getX()&&square.getX()+square.getWidth()<=self.getX()+self.getWidth())){
					if((square.getY()+square.getHeight()>=self.getY()&&square.getY()+square.getHeight()<=self.getY()+self.getHeight())){
						if(Math.abs(x)>Math.abs(y)){
							square.setX(self.getX()-square.getWidth());
						}
						else {
							square.setY(self.getY()-square.getHeight());
						}
						x*=-1f;
						y*=-1f;
					}
					else if(square.getY()<=self.getY()+self.getHeight()&&square.getY()>=self.getY()){
						if(Math.abs(x)>Math.abs(y)){
							square.setX(self.getX()-square.getWidth());
						}
						else {
							square.setY(self.getY()+self.getHeight());							
						}
						x*=-1f;
						y*=-1f;
					}
				}
				else if((square.getX()<=self.getX()+self.getWidth()&&square.getX()>=self.getX())){
					if((square.getY()+square.getHeight()>=self.getY()&&square.getY()+square.getHeight()<=self.getY()+self.getHeight())){
						if(Math.abs(x)>Math.abs(y)){
							square.setX(self.getX()+self.getWidth());
						}
						else {
							square.setY(self.getY()-square.getHeight());
						}
						x*=-1f;
						y*=-1f;
					}
					else if(square.getY()<=self.getY()+self.getHeight()&&square.getY()>=self.getY()){
						if(Math.abs(x)>Math.abs(y)){
							square.setX(self.getX()+self.getWidth());
						}
						else {
							square.setY(self.getY()+self.getHeight());
						}
						x*=-1f;
						y*=-1f;
					}
				}
				
				square.getAction().addFloats(x, y);
			}

		}
		@Override
		public int getIndex() {
			return 2;
		}
		@Override
		public boolean defaultState(){
			return true;
		}
	};

	protected ArrayList<Float> data = new ArrayList<Float>();
	public int numberOfFloats(){
		return 2;
	}
	public void setFloats(Iterator<Float> floats){
		data.clear();
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
	public boolean defaultState(){
		return false;
	}

	public void addFloats(float x, float y) {
		if(data.size()>=numberOfFloats()){
			data.clear();
		}
		data.add(x);
		if(data.size()>=numberOfFloats()){
			data.clear();
		}
		data.add(y);
	}
	public UpdateAction create() {
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		};
		return null;
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
}
