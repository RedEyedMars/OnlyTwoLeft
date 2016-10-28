package com.rem.otl.core.game.environment.update;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rem.otl.core.editor.Button;
import com.rem.otl.core.editor.ButtonAction;
import com.rem.otl.core.editor.Editor;
import com.rem.otl.core.editor.program.Settable;
import com.rem.otl.core.game.Action;
import com.rem.otl.core.game.Game;
import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.game.environment.SquareAction;
import com.rem.otl.core.game.environment.onstep.OnStepAction;
import com.rem.otl.core.game.environment.onstep.OnStepSquare;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.gui.inputs.MouseListener;
import com.rem.otl.core.main.Hub;

public abstract class UpdateAction implements SquareAction<Double,UpdatableSquare>, Iterable<UpdateAction>, Settable{
	public static final int X = 0;
	public static final int Y = 1;
	public static final int LIMIT = 2;
	public static final int START_PERCENT = 3;
	public  static final int LIMIT_FUNCTION = 4;
	public  static final int DEFAULT_STATE = 5;


	public static List<UpdateAction> actions = new ArrayList<UpdateAction>();
	public static List<String> actionNames = new ArrayList<String>();
	public static List<LimiterFunction> limiters = new ArrayList<LimiterFunction>();


	public static final UpdateAction grow = new GrowUpdateAction();
	public static final UpdateAction move = new MoveUpdateAction();
	public static final UpdateAction light = new LightSourceUpdateAction();
	public static final UpdateAction null_action = new NullUpdateAction();

	public final static UpdateAction  combine = new CombinedUpdateActions();

	interface LimiterFunction {
		public float getDelta(double t, double speed, float limit);
		public double getTimeLimit(float speed, float limit);
		public boolean isEndless();
		public Integer getIndex();
	}

	public static final LimiterFunction bounce = new LimiterFunction(){

		@Override
		public float getDelta(double t, double speed, float limit) {
			if(speed==0)return 0f;
			if(limit==0)return 0f;
			float ret = 0f;
			double speedFactor = Math.signum(speed)*speed/limit;
			if(((int)(t*speedFactor/2))%2==0){
				ret = limit*(float) ((1-2.0*Math.sqrt(Math.pow(Math.sin((t*speedFactor-1)*Math.PI/4.0), 2.0))
						)/Math.sqrt(2)+29289.0/99999.0); 
			}
			else {
				ret = limit*(float) ((1-2.0*Math.sqrt(Math.pow(Math.sin((t*speedFactor+1)*Math.PI/4.0), 2.0))
						)/Math.sqrt(2)+29289.0/99999.0);
			}
			//System.out.println(ret);
			return (float) (Math.signum(speed)*ret);
			/* 
			Double ret = 1-Math.sqrt(1-(Math.pow(Math.sin(t/speed*Math.PI/2.0),26.0/14.0)/
					             (Math.sin(t/speed*Math.PI/2.0))));
			if(ret.isNaN()){
				ret = 1-Math.sqrt(1-(Math.pow(Math.sin((t+speed*2)*speed*Math.PI/2.0),26.0/14.0)/
						             (Math.sin((t+1/speed*2)*speed*Math.PI/2.0))));
			}
			if(ret.isNaN())ret=0.0;
			System.out.println(ret);
			return (float)((Math.signum(speed))*ret*limit);/*
			Double ret = 1-Math.sqrt(1-(Math.pow(Math.sin(t*limit*Math.PI/2.0),26.0/14.0)/
					             (Math.sin(t*limit*Math.PI/2.0))));
			if(ret.isNaN()){
				ret = 1-Math.sqrt(1-(Math.pow(Math.sin((t+1/limit*2)*limit*Math.PI/2.0),26.0/14.0)/
						             (Math.sin((t+1/limit*2)*speed*Math.PI/2.0))));
			}
			if(ret.isNaN())return 0f;
			System.out.println(ret);
			return (float)(ret*speed);*/
		}

		@Override
		public double getTimeLimit(float speed, float limit) {
			return -1f;
		}

		@Override
		public boolean isEndless() {
			return true;
		}

		@Override
		public Integer getIndex() {
			return 0;
		}
	};


	public static final LimiterFunction recycle = new LimiterFunction(){
		@Override
		public float getDelta(double t, double speed, float limit) {
			if(speed==0)return 0f;
			if(limit==0)return 0f;
			int distanceTraveled = (int) (speed*t*1000);
			return (distanceTraveled%(limit*1000))/1000f;
		}
		@Override
		public double getTimeLimit(float speed, float limit) {
			return -1f;
		}
		@Override
		public boolean isEndless() {
			return true;
		}
		@Override
		public Integer getIndex() {
			return 1;
		}
	};

	public static final LimiterFunction stop = new LimiterFunction(){

		@Override
		public float getDelta(double t, double speed, float limit) {
			if(speed==0)return 0f;
			if(limit==0)return 0f;
			float distanceTraveled = (float) (speed*t);
			if(Math.abs(distanceTraveled)>=limit){				
				return (float) (limit*Math.signum(speed));
			}
			else return distanceTraveled;
		}
		@Override
		public double getTimeLimit(float speed, float limit) {
			return Math.abs(limit/speed);
		}
		@Override
		public boolean isEndless() {
			return false;
		}
		@Override
		public Integer getIndex() {
			return 2;
		}
	};
	public static final int DEFAULT_STATE_ACTIVATE = 3;
	public static final int DEFAULT_STATE_DEACTIVATE = 4;

	protected UpdatableSquare self;
	protected float x;
	protected float y;
	protected boolean defaultState;
	protected float limit=0f;
	protected float startAtPercent = 0f;
	//protected float limiter = 0f;
	protected int onLimitReachedAction=-1;

	protected double timeSinceStart = 0;
	public void undo() {
	}
	public void loadFrom(Iterator<Integer> ints,Iterator<Float> floats){
		int dstate = ints.next();
		defaultState=dstate==1||dstate==DEFAULT_STATE_ACTIVATE;
		x=floats.next();
		y=floats.next();
		//System.out.println("updateAction.loadFrom "+x+","+y);
		onLimitReachedAction=ints.next();
		if(onLimitReachedAction>=0){
			limit = floats.next();
			startAtPercent = floats.next();
		}
	}
	public float getValue(int index){
		switch(index){
		case X:return x;
		case Y:return y;
		case LIMIT:return limit;
		case START_PERCENT:return startAtPercent;
		}
		return -5;
	}
	public int getInt(int index){
		switch(index){
		case LIMIT_FUNCTION:return onLimitReachedAction;
		case DEFAULT_STATE:return defaultState?DEFAULT_STATE_ACTIVATE:DEFAULT_STATE_DEACTIVATE;
		}
		return -5;
	}

	@Override
	public String getStringValue(int index){
		return null;
	}
	@Override
	public void setValue(int index, String value) {
		throw new RuntimeException("no such string:"+index);
	}
	@Override
	public void setValue(int index, float value) {
		switch(index){
		case X:{x = value;break;}
		case Y:{y = value;break;}
		case LIMIT:{limit = value;break;}
		case START_PERCENT:{startAtPercent = value;break;}
		default: throw new RuntimeException("no such float:"+index);
		}
	}
	@Override
	public void setValue(int index, int value) {
		switch(index){
		case DEFAULT_STATE:{defaultState = value==DEFAULT_STATE_ACTIVATE||value==1;break;}
		case LIMIT_FUNCTION:{onLimitReachedAction = value;break;}
		default: throw new RuntimeException("no such integer:"+index);
		}
	}
	@Override
	public int getValueType(int index){
		return Settable.FLOAT;
	}
	@Override
	public Integer[] copiableValueIds() {
		return new Integer[]{X,Y,LIMIT,START_PERCENT};
	}
	@Override
	public Integer[] copiableIntIds() {
		return new Integer[]{DEFAULT_STATE,LIMIT_FUNCTION};
	}
	@Override
	public String[] copiableValueNames() {
		return new String[]{"X","Y","Limit","Start%"};
	}

	public String[] copiableIntNames() {
		return new String[]{"Default State","Limit Function"};
	}
	public String[] copiableIntTextureNames() {
		return new String[]{"editor_icons","editor_update_limiter_icons"};
	}
	@Override
	public int[] copiableIntTextureRanges(){
		return new int[]{DEFAULT_STATE_ACTIVATE,DEFAULT_STATE_DEACTIVATE+1,-1,3};
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		saveTo.add(defaultState?DEFAULT_STATE_ACTIVATE:DEFAULT_STATE_DEACTIVATE);
		saveTo.add(Math.abs(x)>0.000000001f?x:0f);
		saveTo.add(Math.abs(y)>0.000000001f?y:0f);
		saveTo.add(onLimitReachedAction);

		//System.out.println("updateAction.saveTo "+x+","+y+" "+this);
		if(onLimitReachedAction!=-1){
			saveTo.add(limit);
			saveTo.add(Math.abs(startAtPercent)>0.000000001f?startAtPercent:0f);
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

	public boolean hasReachedLimit() {
		if(onLimitReachedAction==-1)return false;
		return (limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(x*1),limit)==(float)(limit*Math.signum(x))&&x!=0) || (y!=0&&
				limiters.get(onLimitReachedAction).getDelta(timeSinceStart,(y*1),limit)==(float)(limit*Math.signum(y)));
	}
	public void onActivate(){
	}
	public void onDeactivate(){
	}

	public void flip(){
		y=-y;
	}

	public double getTimeSinceStart() {
		return timeSinceStart;
	}
	public double getTimeToLimit() {
		if(onLimitReachedAction>-1||!limiters.get(onLimitReachedAction).isEndless()){
			return limiters.get(onLimitReachedAction).getTimeLimit(getSpeed(),limit);
		}
		else {
			return timeSinceStart;
		}
	}
	public float getSpeed() {
		return (float) Math.sqrt(x*x+y*y);
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
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}};
	}

	protected void move(float dx, float dy) {
		for(Hero hero:Hub.getBothHeroes()){
			OnStepAction action = self.getOnHitAction(hero);
			if(action!=null){
				if(action.getIndex()==2){
					continue;
				}				


				if(!action.isPassible()){
					hero.reposition(hero.getX()+hero.getDeltaX(),
							hero.getY()+hero.getDeltaY());
				}
				boolean isWithin = hero.isWithin(self);
				if(!action.isPassible()){
					hero.reposition(hero.getX()-hero.getDeltaX(),
							hero.getY()-hero.getDeltaY());
				}
				if(isWithin){
					hero.setDeltaX(hero.getDeltaX()+dx);
					hero.setDeltaY(hero.getDeltaY()+dy);
				}
			}
		}
	}
	public int saveType(){
		return 4;
	}
	public abstract UpdateAction create(); 
	static {
		try {
			Map<Integer,UpdateAction> uas = new HashMap<Integer,UpdateAction>();
			Map<Integer,LimiterFunction> lfs = new HashMap<Integer,LimiterFunction>();
			Map<Integer,String> names = new HashMap<Integer,String>();
			for(Field field:UpdateAction.class.getFields()){
				Object obj = field.get(UpdateAction.class);
				if(obj instanceof UpdateAction){
					//System.out.println(field.getName());
					UpdateAction ua = (UpdateAction) obj;
					uas.put(ua.getIndex(),ua);
					names.put(ua.getIndex(),field.getName());
				}
				else if(obj instanceof LimiterFunction){
					//System.out.println(field.getName());
					LimiterFunction lf = (LimiterFunction) obj;
					lfs.put(lf.getIndex(), lf);
				}
			}
			for(int i=0;i<uas.size();++i){
				actions.add(uas.get(i));
				actionNames.add(names.get(i));
			}
			for(int i=0;i<lfs.size();++i){
				limiters.add(lfs.get(i));
			}
		}
		catch (IllegalArgumentException e){			
			e.printStackTrace();
		}
		catch  (IllegalAccessException e) {
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
	public static String getActionName(int i) {
		if(i==-1||i>=actions.size()){
			return null;
		}
		else {
			if(i==-2){
				return "combine";
			}
			else {
				return actionNames.get(i);
			}
		}
	}
}
