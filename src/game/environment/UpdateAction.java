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
	public static List<OnStepAction> limiters = new ArrayList<OnStepAction>();


	protected UpdatableSquare self;
	public static final UpdateAction grow = new UpdateAction(){
		private float growthW = 0f;
		private float growthH = 0f;
		{
			defaultState = false;
		}
		@Override
		public void act(Double seconds) {
			growthW += x*seconds;
			growthH += y*seconds;
			self.adjust((float) (self.getWidth()+x*seconds), (float) (self.getHeight()+y*seconds));
			if(onLimitBrokenAction>-1&&Math.sqrt(growthW*growthW+growthH*growthH)>=limit){
				limiters.get(onLimitBrokenAction).setTarget(self);
				limiters.get(onLimitBrokenAction).act(null);
				growthW=0f;
				growthH=0f;
			}
		}
		@Override
		public void undo(){
			self.adjust(self.getWidth()-growthW, self.getHeight()-growthH);
		}
		@Override
		public int getIndex() {
			return 0;
		}
	};

	public static final UpdateAction move = new UpdateAction(){
		{
			defaultState = true;
		}
		private float movementX = 0f;
		private float movementY = 0f;
		private float origXvel = 0f;
		private float origYvel = 0f;
		@Override
		public void act(Double seconds) {
			movementX += x*seconds;
			movementY += y*seconds;
			self.move((float) (x*seconds),(float) (y*seconds));
			if(onLimitBrokenAction>-1&&Math.sqrt(movementX*movementX+movementY*movementY)>=limit){
				limiters.get(onLimitBrokenAction).setTarget(self);
				limiters.get(onLimitBrokenAction).act(null);
				movementX=0f;
				movementY=0f;
			}
		}
		@Override
		public void undo(){
			self.setX(self.getX()-movementX);
			self.setY(self.getY()-movementY);
			addFloats(origXvel,origYvel);
		}
		@Override
		public void setArgs(Iterator<Integer> ints,Iterator<Float> floats){
			super.setArgs(ints,floats);
			origXvel=getFloat(0);
			origYvel=getFloat(1);
		}
		@Override
		public int getIndex() {
			return 1;
		}
	};

	public static final OnStepAction reverse = new OnStepAction(){
		@Override
		public void act(Hero hero) {
			UpdatableSquare square = (UpdatableSquare)target;
			square.getAction().addFloats(-square.getAction().x, -square.getAction().y);

		}
		@Override
		public int getIndex() {
			return 0;
		}
	};

	public static final OnStepAction recycle = new OnStepAction(){
		@Override
		public void act(Hero hero) {
			UpdatableSquare square = (UpdatableSquare)target;
			square.recycle();
		}
		@Override
		public int getIndex() {
			return 1;
		}
	};
	
	public static final OnStepAction stop = new OnStepAction(){
		@Override
		public void act(Hero hero) {
			UpdatableSquare square = (UpdatableSquare)target;
			square.getAction().addFloats(0, 0);
			square.deactivate();
		}
		@Override
		public int getIndex() {
			return 2;
		}
	};

	protected float x;
	protected float y;
	protected boolean defaultState;
	protected float limit=0f;
	protected int onLimitBrokenAction=-1;
	public void undo() {
	}
	public void setArgs(Iterator<Integer> ints,Iterator<Float> floats){		
		defaultState=ints.next()==1;
		x=floats.next();
		y=floats.next();
		onLimitBrokenAction=ints.next();
		if(onLimitBrokenAction>=0){
			limit = floats.next();
		}
	}
	public float getFloat(int i){
		return i==0?x:i==1?y:limit;
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		saveTo.add(defaultState?1:0);
		saveTo.add(x);
		saveTo.add(y);
		saveTo.add(onLimitBrokenAction);
		if(onLimitBrokenAction!=-1){
			saveTo.add(limit);
		}
	}
	@Override
	public int numberOfTargets() {
		return 0;
	}
	@Override
	public void setTarget(Square square) {
		this.self = (UpdatableSquare) square;
	}

	public boolean getDefaultState(){
		return defaultState;
	}
	public void setDefaultState(boolean newState) {
		this.defaultState = newState;
	}

	public void addFloats(float x, float y) {
		this.x=x;
		this.y=y;
	}
	public void setLimit(float limit){
		this.limit = limit;
	}

	public int getLimiter() {
		return onLimitBrokenAction;
	}
	public void setLimiter(int limiter) {
		this.onLimitBrokenAction=limiter;
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
				else if(obj instanceof OnStepAction){
					//System.out.println(field.getName());
					limiters.add((OnStepAction) obj);
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
