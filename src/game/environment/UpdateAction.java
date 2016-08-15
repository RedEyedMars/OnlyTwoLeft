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
import game.Game;

public abstract class UpdateAction implements SquareAction<Double>, Iterable<UpdateAction>{
	public static List<UpdateAction> actions = new ArrayList<UpdateAction>();
	public static List<String> actionNames = new ArrayList<String>();
	public static List<Action<UpdateAction>> limiters = new ArrayList<Action<UpdateAction>>();


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
				limiters.get(onLimitBrokenAction).act(this);
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
			if(onLimitBrokenAction>-1&&Math.sqrt(movementX*movementX+movementY*movementY)>=limit){
				limiters.get(onLimitBrokenAction).act(this);
				movementX=0f;
				movementY=0f;
			}
			else {
				self.move((float) (x*seconds),(float) (y*seconds));
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
	public static final UpdateAction light = new UpdateAction(){
		{
			defaultState = true;
		}
		private Square blackLight = null;
		private UpdatableSquare blackReflector = null;
		private Square whiteLight = null;
		private UpdatableSquare whiteReflector = null;
		private float minDis;
		@Override
		public void act(Double seconds) {
			if(!self.isVisible())return;
			boolean horizontal = x!=0;

			Object closest =getClosest(true,horizontal);//can be null
			OnStepSquare closestBlack = null;
			Hero closestBlackHero = null;
			if(closest!=null){
				if(closest instanceof OnStepSquare){
					closestBlack=(OnStepSquare)closest;
				}
				else if(closest instanceof Hero) {
					closestBlackHero = (Hero)closest;
				}
			}
			float blackDis = minDis;
			closest = getClosest(false,horizontal);//can be null
			OnStepSquare closestWhite = null;
			Hero closestWhiteHero = null;
			if(closest!=null){
				if(closest instanceof OnStepSquare){
					closestWhite=(OnStepSquare)closest;
				}
				else if(closest instanceof Hero) {
					closestWhiteHero = (Hero)closest;
				}
			}
			float whiteDis = minDis;
			if(horizontal){
				if(blackLight==null){
					blackLight = new OnStepSquare(0,self.getColour(0),-1,blackDis,self.getHeight(),-1,-1);
					whiteLight = new OnStepSquare(0,-1,self.getColour(1),whiteDis,self.getHeight(),-1,-1);
					Hub.map.displaySquare(blackLight);
					Hub.map.displaySquare(whiteLight);
					self.addChild(blackLight);
					self.addChild(whiteLight);
					blackLight.onAddToDrawable();
					whiteLight.onAddToDrawable();
				}
				else {
					blackLight.adjust(blackDis,self.getHeight());
					whiteLight.adjust(whiteDis,self.getHeight());
					blackLight.setVisible(true);
					whiteLight.setVisible(true);
				}				
				if(x>0){
					blackLight.setX(self.getX()+self.getWidth());
					whiteLight.setX(self.getX()+self.getWidth());
				}
				else {
					blackLight.setX(self.getX()-blackDis);
					whiteLight.setX(self.getX()-whiteDis);
				}
				blackLight.setY(self.getY());
				whiteLight.setY(self.getY());
			}
			else {
				if(blackLight==null){
					blackLight = new Square(self.getColour(0),-1,self.getWidth(),blackDis);
					whiteLight = new Square(-1,self.getColour(1),self.getWidth(),whiteDis);
					Hub.map.displaySquare(blackLight);
					Hub.map.displaySquare(whiteLight);
					self.addChild(blackLight);
					self.addChild(whiteLight);
					blackLight.onAddToDrawable();
					whiteLight.onAddToDrawable();
				}
				else {
					blackLight.adjust(self.getWidth(),blackDis);
					whiteLight.adjust(self.getWidth(),whiteDis);
					blackLight.setVisible(true);
					whiteLight.setVisible(true);
				}
				if(y>0){
					blackLight.setY(self.getY()+self.getHeight());
					whiteLight.setY(self.getY()+self.getHeight());
				}
				else {
					blackLight.setY(self.getY()-blackDis);
					whiteLight.setY(self.getY()-whiteDis);
				}
				blackLight.setX(self.getX());
				whiteLight.setX(self.getX());
			}
			blackReflector = makeReflector(closestBlack,blackReflector,0,seconds);
			whiteReflector = makeReflector(closestWhite,whiteReflector,1,seconds);
			handleHero(closestBlackHero,self.getColour(0));
			handleHero(closestWhiteHero,self.getColour(1));
		}
		private void handleHero(Hero hero,int colour){
			if(hero!=null){
				hero.addToColour(self);
			}
		}
		private UpdatableSquare makeReflector(OnStepSquare closest,UpdatableSquare reflector,int colour,double seconds) {
			if(self.getColour(colour)!=-1&&closest!=null&&closest.getReflectTriangle(closest.getColour(colour))!=-1){
				int reflectorShape = closest.getReflectTriangle(closest.getColour(colour));
				float reflectorX = x;
				float reflectorY = y;
				boolean show=true;
				if(reflectorShape==2){
					if(y>0||x<0){
						show=false;
					}
					else {
						reflectorX=y;
						reflectorY=x;
					}
				}
				else if(reflectorShape==3){
					if(y>0||x>0){
						show=false;
					}
					else {
						reflectorX=-y;
						reflectorY=-x;
					}
				}
				else if(reflectorShape==4){
					if(y<0||x<0){
						show=false;
					}
					else {
						reflectorX=-y;
						reflectorY=-x;
					}
				}
				else if(reflectorShape==5){
					if(y<0||x>0){
						show=false;
					}
					else {
						reflectorX=y;
						reflectorY=x;
					}
				}

				if(show){
					if(reflector==null){
						if(colour==0){
							reflector=new UpdatableSquare(reflectorShape,
									self.getColour(colour),-1,
									closest.getWidth(),closest.getHeight(),getIndex(),reflectorX,reflectorY,-1,-1);
						}
						else if(colour==1){
							reflector=new UpdatableSquare(reflectorShape,
									-1,self.getColour(colour),
									closest.getWidth(),closest.getHeight(),getIndex(),reflectorX,reflectorY,-1,-1);

						}
						Hub.map.displaySquare(reflector);
						self.addChild(reflector);
						reflector.onAddToDrawable();
					}
					else {
						reflector.setFrame(self.getColour(colour));
						reflector.setShape(reflectorShape);
						reflector.adjust(closest.getWidth(),closest.getHeight());
						reflector.getAction().addFloats(reflectorX, reflectorY);
					}
					reflector.setX(closest.getX());
					reflector.setY(closest.getY());
					reflector.setVisible(true);
					reflector.getAction().act(seconds);
				}
				else if(reflector!=null){
					reflector.setVisible(false);
				}
			}
			else if(reflector!=null){
				reflector.setVisible(false);
			}
			return reflector;
		}
		private GraphicEntity getClosest(boolean black, boolean horizontal){
			List<OnStepSquare> squares = Hub.map.getFunctionalSquares();
			GraphicEntity stopSquare = null;
			minDis=1000f;
			/*Hero hero=null;
			if(black){
				hero=Game.black;
			}
			else {
				hero=Game.white;
			}*/
			for(Hero hero:new Hero[]{Game.black,Game.white}){
				if(hero!=null){
					if(horizontal&&
							(self.getY()+self.getHeight()/2f>=hero.getY()&&
							self.getY()+self.getHeight()/2f<=hero.getY()+hero.getHeight()||
							hero.getY()+hero.getHeight()/2f>=self.getY()&&
							hero.getY()+hero.getHeight()/2f<=self.getY()+self.getHeight())){
						if(x>0&&hero.getX()-(self.getX()+self.getWidth())<minDis&&
								self.getX()+self.getWidth()<=hero.getX()){
							minDis=hero.getX()-(self.getX()+self.getWidth());
							stopSquare=hero;
						}
						else if(x<0&&self.getX()-(hero.getX()+hero.getWidth())<minDis&&
								hero.getX()+hero.getWidth()<=self.getX()){
							minDis=self.getX()-(hero.getX()+hero.getWidth());
							stopSquare=hero;
						}
					}
					else if(!horizontal&&
							(self.getX()+self.getWidth()/2f>=hero.getX()&&
							self.getX()+self.getWidth()/2f<=hero.getX()+hero.getWidth()||
							hero.getX()+hero.getWidth()/2f>=self.getX()&&
							hero.getX()+hero.getWidth()/2f<=self.getX()+self.getWidth())){
						if(y>0&&hero.getY()-(self.getY()+self.getHeight())<minDis&&
								self.getY()+self.getHeight()-0.005f<=hero.getY()){
							minDis=hero.getY()-(self.getY()+self.getHeight());
							stopSquare=hero;
						}
						else if(y<0&&self.getY()-(hero.getY()+hero.getHeight())<minDis&&
								hero.getY()+hero.getHeight()<=self.getY()){
							minDis=self.getY()-(hero.getY()+hero.getHeight());
							stopSquare=hero;
						}
					}
				}
			}
			for(int i=squares.size()-1;i>=0;--i){
				if(squares.get(i)==self)continue;
				OnStepSquare square = squares.get(i);
				if(black?(square.getBlackAction()!=null&&!square.getBlackAction().isSafe()):(square.getWhiteAction()!=null&&!square.getWhiteAction().isSafe())){
					if(horizontal&&
							(self.getY()+self.getHeight()/2f>=square.getY()&&
							self.getY()+self.getHeight()/2f<=square.getY()+square.getHeight()||
							square.getY()+square.getHeight()/2f>=self.getY()&&
							square.getY()+square.getHeight()/2f<=self.getY()+self.getHeight())){
						if(x>0&&square.getX()-(self.getX()+self.getWidth())<minDis&&
								self.getX()+self.getWidth()<=square.getX()){
							minDis=square.getX()-(self.getX()+self.getWidth());
							stopSquare=square;
						}
						else if(x<0&&self.getX()-(square.getX()+square.getWidth())<minDis&&
								square.getX()+square.getWidth()<=self.getX()){
							minDis=self.getX()-(square.getX()+square.getWidth());
							stopSquare=square;
						}
					}
					else if(!horizontal&&
							(self.getX()+self.getWidth()/2f>=square.getX()&&
							self.getX()+self.getWidth()/2f<=square.getX()+square.getWidth()||
							square.getX()+square.getWidth()/2f>=self.getX()&&
							square.getX()+square.getWidth()/2f<=self.getX()+self.getWidth())){
						if(y>0&&square.getY()-(self.getY()+self.getHeight())<minDis&&
								self.getY()+self.getHeight()<=square.getY()){
							minDis=square.getY()-(self.getY()+self.getHeight());
							stopSquare=square;
						}
						else if(y<0&&self.getY()-(square.getY()+square.getHeight())<minDis&&
								square.getY()+square.getHeight()<=self.getY()){
							minDis=self.getY()-(square.getY()+square.getHeight());
							stopSquare=square;
						}
					}
				}
			}
			return stopSquare;
		}
		@Override
		public void undo(){
			if(blackLight!=null){
				blackLight.setVisible(false);
			}
			if(whiteLight!=null){
				whiteLight.setVisible(false);
			}
			if(blackReflector!=null){
				blackReflector.setVisible(false);
			}
			if(whiteReflector!=null){
				whiteReflector.setVisible(false);
			}
		}
		@Override
		public void onDeactivate(){
			undo();
		}
		@Override
		public int getIndex() {
			return 2;
		}
	};

	public final static UpdateAction  combine = new UpdateAction(){

		List<UpdateAction> actions = new ArrayList<UpdateAction>();
		@Override
		public void setArgs(Iterator<Integer> ints, Iterator<Float> floats){
			this.defaultState=true;
			int size = ints.next();
			for(int i=0;i<size;++i){
				UpdateAction action = UpdateAction.getAction(ints.next()).create();
				action.setArgs(ints, floats);
				action.setTarget(self);
				actions.add(action);
			}
		}
		@Override
		public void saveTo(List<Object> toSave){
			toSave.add(getIndex());
			toSave.add(actions.size());
			for(UpdateAction action:actions){
				action.saveTo(toSave);
			}
		}
		@Override
		public int getIndex() {
			return -2;
		}

		@Override
		public void act(Double subject) {
			for(UpdateAction action:actions){
				action.act(subject);
			}
		}
		@Override
		public void setTarget(Square target){
			super.setTarget(target);
			for(UpdateAction action:actions){
				action.setTarget(target);
			}
		}
		@Override
		public void undo(){
			for(UpdateAction action:actions){
				action.undo();
			}
		}
		@Override
		public int numberOfTargets(){
			return actions.size();
		}

		public Iterator<UpdateAction> iterator(){
			return new Iterator<UpdateAction>(){
				private int index = 0;
				private Iterator<UpdateAction> currentIterator=null;
				{
					if(index<actions.size()){
						currentIterator = actions.get(index++).iterator();
					}
				}
				@Override
				public boolean hasNext() {
					if(currentIterator==null)return false;
					return currentIterator.hasNext()||index<actions.size();
				}

				@Override
				public UpdateAction next() {
					while(!currentIterator.hasNext()&&index<actions.size()){
						currentIterator=actions.get(index++).iterator();
					}
					if(currentIterator.hasNext()){
						return currentIterator.next();
					}
					else {
						return null;
					}
				}};
		}
	};

	public static final Action<UpdateAction> reverse = new Action<UpdateAction>(){
		@Override
		public void act(UpdateAction action) {
			action.addFloats(-action.x, -action.y);
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
			action.addFloats(0, 0);
			action.self.deactivate();
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
		return 1;
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
	public void onActivate(){
		
	}
	public void onDeactivate(){
		
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
