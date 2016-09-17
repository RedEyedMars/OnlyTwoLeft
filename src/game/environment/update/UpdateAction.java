package game.environment.update;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import editor.Button;
import editor.ButtonAction;
import editor.Editor;
import game.environment.Square;
import game.environment.SquareAction;
import game.environment.onstep.OnStepAction;
import game.environment.onstep.OnStepSquare;
import game.hero.Hero;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;
import main.Hub;
import game.Action;
import game.Game;

public abstract class UpdateAction implements SquareAction<Double,UpdatableSquare>, Iterable<UpdateAction>{
	public static List<UpdateAction> actions = new ArrayList<UpdateAction>();
	public static List<Action<UpdateAction>> limiters = new ArrayList<Action<UpdateAction>>();


	public static final UpdateAction grow = new GrowUpdateAction();
	public static final UpdateAction move = new MoveUpdateAction();
	public static final UpdateAction light = new LightSourceUpdateAction();
	public static final UpdateAction null_action = new NullUpdateAction();

	public final static UpdateAction  combine = new CombinedUpdateActions();

	public static final Action<UpdateAction> reverse = new Action<UpdateAction>(){
		@Override
		public void act(UpdateAction action) {
			action.setFloats(-action.x, -action.y);
		}
	};

	public static final Action<UpdateAction> recycle = new Action<UpdateAction>(){
		@Override
		public void act(UpdateAction action) {
			action.undo();
		}
	};

	public static final Action<UpdateAction> stop = new Action<UpdateAction>(){
		@Override
		public void act(UpdateAction action) {
			action.self.deactivate();
			action.setFloats(0, 0);
		}
	};

	protected UpdatableSquare self;
	protected float x;
	protected float y;
	protected boolean defaultState;
	protected float limit=0f;
	protected float limiterStartPercent = 0f;
	protected float limiter = 0f;
	protected int onLimitBrokenAction=-1;
	public void undo() {
	}
	public void loadFrom(Iterator<Integer> ints,Iterator<Float> floats){		
		defaultState=ints.next()==1;
		x=floats.next();
		y=floats.next();
		onLimitBrokenAction=ints.next();
		if(onLimitBrokenAction>=0){
			limit = floats.next();
			limiterStartPercent = floats.next();
		}
	}
	public float getFloat(int i){
		return i==0?x:i==1?y:i==2?limit:limiterStartPercent;
	}

	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		saveTo.add(defaultState?1:0);
		saveTo.add(Math.abs(x)>0.000000001f?x:0f);
		saveTo.add(Math.abs(y)>0.000000001f?y:0f);
		saveTo.add(onLimitBrokenAction);
		if(onLimitBrokenAction!=-1){
			saveTo.add(limit);
			saveTo.add(Math.abs(limiterStartPercent)>0.000000001f?x:0f);
		}
	}
	@Override
	public int targetType() {
		return 1;
	}
	@Override
	public void setTarget(UpdatableSquare square) {
		this.self = square;
	}

	public boolean getDefaultState(){
		return defaultState;
	}
	public void setDefaultState(boolean newState) {
		this.defaultState = newState;
	}

	public void setFloats(float x, float y) {
		this.x=x;
		this.y=y;
	}
	public void setX(Float x) {
		this.x = x;
	}
	public void setY(Float y) {
		this.y = y;
	}
	public void setLimit(float limit){
		this.limit = limit;
	}
	public void setLimiterStartPercent(float limiterStartPercent){
		this.limiterStartPercent = limiterStartPercent;
	}

	public int getLimiter() {
		return onLimitBrokenAction;
	}
	public void setLimiter(int limiter) {
		this.onLimitBrokenAction=limiter;
	}

	public boolean hasCrestedLimit() {
		return limiter==0f;
	}
	public void onActivate(){
		limiter=limit*limiterStartPercent;
	}
	public void onDeactivate(){

	}
	public void flip() {

	}
	public Iterator<UpdateAction> iterator(){
		final UpdateAction self = this;
		return new Iterator<UpdateAction>(){
			private boolean sent = false;
			@Override
			public boolean hasNext() {
				return !sent;
			}

			@Override
			public UpdateAction next() {
				sent = true;
				return self;
			}};
	}

	protected void move(float dx, float dy) {
		for(Hero hero:Hub.getBothHeroes()){
			OnStepAction action = self.getOnHitAction(hero);
			if(action!=null){
				if(action.getIndex()==2){
					continue;
				}				
			}
			if(hero.getY()>=self.getY()+self.getHeight()){
				hero.reposition(hero.getX()+hero.getDeltaX(),
						  hero.getY()+hero.getDeltaY());
				if(hero.isWithin(self)){
					//hero.setXVelocity(hero.getXVelocity()+dx);
					hero.move(dx,dy);
				}
				hero.reposition(hero.getX()-hero.getDeltaX(),
						  hero.getY()-hero.getDeltaY());
			}
		}
	}
	public int saveType(){
		return 4;
	}
	public abstract UpdateAction create(); 
	static {
		try {
			for(Field field:UpdateAction.class.getFields()){
				Object obj = field.get(UpdateAction.class);
				if(obj instanceof UpdateAction){
					//System.out.println(field.getName());
					actions.add((UpdateAction) obj);
				}
				else if(obj instanceof Action){
					//System.out.println(field.getName());
					limiters.add((Action<UpdateAction>) obj);
				}
			} 
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static UpdateAction getAction(Integer i) {
		//System.out.println(i);
		if(i==-1||i>=actions.size()){
			return null;
		}
		else {
			if(i==-2){
				return combine;
			}
			else {
				return actions.get(i);
			}
		}
	}

}
