package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import duo.client.Client;
import game.Action;
import game.Hero;
import game.VisionBubble;
import game.modes.GameMode;
import game.modes.OverheadMode;
import game.modes.PlatformMode;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import main.Hub;
import main.Log;

public class Map extends GraphicEntity {

	private static Map overhead = new Map(){
		{
			setMapId(-20);
		}
	};
	private static Map platform = new Map(){
		{
			setMapId(-40);
		}
	};
	private List<Square> allSquares = new ArrayList<Square>();
	private List<OnStepSquare> functionalSquares = new ArrayList<OnStepSquare>();
	private List<UpdatableSquare> updateSquares = new ArrayList<UpdatableSquare>();
	private List<OnCreateSquare> onCreates = new ArrayList<OnCreateSquare>();
	private List<Square> templateSquares = new ArrayList<Square>();

	private java.util.Map<Square,List<OnStepSquare>> adjacentSquares = new HashMap<Square,List<OnStepSquare>>();


	protected static final float gridSizeX = 20f;
	protected static final float gridSizeY = 20f;

	private float[] startingXPosition = new float[2];
	private float[] startingYPosition = new float[2];


	private int mapId=0;
	private Map() {
		super("blank");
		this.setVisible(false);
	}

	public List<OnStepSquare> getFunctionalSquares() {
		return functionalSquares;
	}

	public List<UpdatableSquare> getUpdateSquares() {
		return updateSquares;
	}

	public void addSquare(Square square){
		if(square==null)return;
		displaySquare(square);
		allSquares.add(square);
		addChild(square);
	}


	public void displaySquare(Square square){
		if(square==null)return;
		if(square.isFunctional()){
			functionalSquares.add((OnStepSquare)square);
		}
		if(square instanceof UpdatableSquare){
			updateSquares.add((UpdatableSquare)square);
		}
		if(square instanceof OnCreateSquare){
			onCreates.add((OnCreateSquare) square);
		}
	}

	public void setVisibleSquares(int colour){
		for(Square square:allSquares){
			square.displayFor(colour);
		}
	}

	private float xOffset = 0f;
	private float yOffset = 0f;
	@Override
	public void setX(float x){
		xOffset = x-getX();
		super.setX(x);
	}
	@Override
	public void setY(float y){
		yOffset = y-getY();
		super.setY(y);
	}
	@Override
	public float offsetX(int index){
		if(index!=0){
			return getChild(index).getX()+xOffset-getX();
		}
		else {
			return -getX();
		}
	}
	@Override
	public float offsetY(int index){
		if(index!=0){
			return getChild(index).getY()+yOffset-getY();
		}
		else {
			return -getY();
		}
	}

	public boolean isMallible() {
		return true;
	}

	public List<Square> getSquares() {
		return allSquares;
	}

	public void onCreate() {
		for(OnCreateSquare square:onCreates){
			square.act();
		}
		for(OnStepSquare square:functionalSquares){
			List<OnStepSquare> list = new ArrayList<OnStepSquare>();
			for(OnStepSquare adj:functionalSquares){
				if(square!=adj){
					if(square.isWithin(adj.getX(), adj.getY())||
							square.isWithin(adj.getX()+adj.getWidth(), adj.getY()+adj.getHeight())||
							square.isWithin(adj.getX()+adj.getWidth(), adj.getY())||
							square.isWithin(adj.getX(), adj.getY()+adj.getHeight())){
						list.add(adj);
					}
					else if(adj.isWithin(square.getX(), square.getY())||
							adj.isWithin(square.getX()+square.getWidth(), square.getY()+square.getHeight())||
							adj.isWithin(square.getX()+square.getWidth(), square.getY())||
							adj.isWithin(square.getX(), square.getY()+square.getHeight())){
						list.add(adj);
					}
				}
			}
			adjacentSquares.put(square,list);
		}
	}
	public List<OnStepSquare> getAdjacentSquares(OnStepSquare q) {
		return adjacentSquares.get(q);
	}

	public List<Square> getTemplateSquares() {
		return templateSquares;
	}

	public void addTemplateSquare(Square square) {
		templateSquares.add(square);
	}
	public Square isWithinWall(Square target, Hero accordingTo) {
		for(int i=functionalSquares.size()-1;i>=0;--i){
			OnStepSquare square = functionalSquares.get(i);
			if(square==target){
				continue;
			}
			if(target.isCompletelyWithin(square)&&square.getOnHitAction(accordingTo).isSafe()){			
				return null;
			}
			else if(square.getOnHitAction(accordingTo).getIndex()==1
					&&(square.isWithin(target))){
				return square;
			}
		}
		return null;
	}

	public static Map createMap(int id){
		try {
			if(id==-40){
				return platform.getClass().newInstance();
			}
			else if(id==-20){
				return overhead.getClass().newInstance();
			}
			else if(id==0){
				Map map = new Map();
				map.setMapId(-20);
				return map;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void load(Object[] loaded) {
		Hub.map = null;
		MapLoader loader = null;
		if(loaded[3] instanceof Integer){
			Hub.map = createMap(((Integer)loaded[3]));
		}
		if(Hub.map==null){
			Hub.map = createMap(0);
			loader = Hub.map.new MapLoader(loaded);
		}
		else {
			loader = Hub.map.new MapLoader(loaded);
			loader.nextInteger();
		}
		int i=0;
		for(Iterator<Float> itr=loader.getFloats();i<2;++i){
			Hub.map.startingXPosition[i]=itr.next();
			Hub.map.startingYPosition[i]=itr.next();
		}
		int size = loader.nextInteger();
		for(i=0;i<size;++i){
			Hub.map.templateSquares.add(Square.create(loader.getIntegers(), loader.getFloats()));
		}
		for(Square square:loader){
			Hub.map.addSquare(square);
		}
	}

	public void moveToStart(Hero hero){
		hero.setX(startingXPosition[hero.getColour()]);
		hero.setY(startingYPosition[hero.getColour()]);
	}

	public void setStartPosition(int colour, float x, float y) {
		startingXPosition[colour]=x;
		startingYPosition[colour]=y;
	}
	public float getStartingXPosition(int colour) {
		return startingXPosition[colour];
	}
	public float getStartingYPosition(int colour) {
		return startingYPosition[colour];
	}
	public float getRealX(int x){
		return ((float)x)/gridSizeX;
	}
	public int getIntXHigh(float x){
		return (int) (x*gridSizeX+0.5f);
	}
	public int getIntXLow(float x){
		if(x<=0){
			return (int) (x*gridSizeX-0.5f);
		}
		return (int) (x*gridSizeX+0.5f);
	}

	public float getRealY(int y){
		return ((float)y)/gridSizeY;
	}
	public int getIntYHigh(float y){
		return (int) (y*gridSizeY+0.5f);
	}
	public int getIntYLow(float y){
		return (int) (y*gridSizeY+0.5f);
	}

	public int getMapId(){
		return mapId;
	}
	public void setMapId(int id){
		this.mapId = id;
	}

	public GameMode getGameMode() {
		switch((mapId+20)/-20){
			case 0:{
				return new OverheadMode();
			}
			case 1:{
				return new PlatformMode();
			}
		}
		return null;
	}

	private class MapLoader implements Iterable<Square>, Iterator<Square> {
		private int maxIntegers;
		private int integerIndex = 3;
		private int floatIndex = 0;
		private int stringIndex = 0;
		private Object[] data;
		private Square currentSquare = null;

		private Iterator<Integer> integerIterator = new Iterator<Integer>(){
			@Override
			public boolean hasNext() {
				return integerIndex<maxIntegers;
			}
			@Override
			public Integer next() {
				return nextInteger();
			}			
		};
		private Iterator<Float> floatIterator = new Iterator<Float>(){
			@Override
			public boolean hasNext() {
				return floatIndex<stringIndex;
			}

			@Override
			public Float next() {
				return nextFloat();
			}
		};
		private Iterator<String> stringIterator = new Iterator<String>(){
			@Override
			public boolean hasNext() {
				return stringIndex<data.length;
			}

			@Override
			public String next() {
				return nextString();
			}
		};


		public MapLoader(Object[] loaded){
			this.data = loaded;
			integerIndex = 3;
			maxIntegers = (Integer)data[0];
			floatIndex = maxIntegers;
			stringIndex = floatIndex+(Integer)data[1];
		}
		public void add(Square square){
			this.currentSquare = square;
		}
		@Override
		public boolean hasNext() {
			return integerIndex+1<maxIntegers;
		}

		@Override
		public Square next() {
			return Square.create(getIntegers(), getFloats());
		}

		public Integer nextInteger(){
			return (Integer)data[integerIndex++];
		}

		public Float nextFloat(){
			return (Float)data[floatIndex++];
		}

		public String nextString(){
			return (String)data[stringIndex++];
		}
		@Override
		public Iterator<Square> iterator() {
			return this;
		}

		public Iterator<Integer> getIntegers(){
			return integerIterator;
		}
		public Iterator<Float> getFloats(){
			return floatIterator;
		}
		public Iterator<String> getStrings(){
			return stringIterator;
		}

	}




}
