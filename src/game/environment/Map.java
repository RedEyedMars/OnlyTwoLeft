package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.Hero;
import gui.graphics.GraphicEntity;
import main.Log;

public class Map extends GraphicEntity {

	private List<Square> allSquares = new ArrayList<Square>();
	private List<Square> blackSquares = new ArrayList<Square>();
	private List<Square> whiteSquares = new ArrayList<Square>();
	private List<FunctionalSquare> functionalSquares = new ArrayList<FunctionalSquare>();
	private List<OnCreateSquare> onCreates = new ArrayList<OnCreateSquare>();

	private float[] startingXPosition = new float[2];
	private float[] startingYPosition = new float[2];
	public Map() {
		super("blank");
		this.setVisible(false);
	}

	public List<FunctionalSquare> functionalSquares() {
		return functionalSquares;
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
			functionalSquares.add((FunctionalSquare)square);
		}
		if(square.visibleToBlack()&&!square.visibleToWhite()){
			blackSquares.add(square);
		}
		else if(square.visibleToWhite()&&!square.visibleToBlack()){
			whiteSquares.add(square);
		}
		if(square instanceof OnCreateSquare){
			onCreates.add((OnCreateSquare) square);
		}
	}

	public void setVisibleSquares(int colour){
		List<Square> on = null;
		List<Square> off = null;
		if(colour==0){
			on = blackSquares;
			off = whiteSquares;
		}
		else if(colour==1){
			on = whiteSquares;
			off = blackSquares;
		}
		for(Square offSquare:off){
			offSquare.turnOff();
		}
		for(Square onSquare:on){
			onSquare.turnOn();
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
	}

	public boolean isWithinWall(Square target) {
		for(FunctionalSquare square:functionalSquares){
			if(square.getActions().contains(SquareAction.actions.get(0))
					&&(target.isCompletelyWithin(square)||
						target.isCompletelyWithin(square))){
				return false;
			}
		}
		for(FunctionalSquare square:functionalSquares){
			if(square.getActions().contains(SquareAction.actions.get(1))
					&&(square.isWithin(target.getX(), target.getY())||
					   square.isWithin(target.getX()+target.getWidth(), target.getY()+target.getHeight()))){
				return true;
			}
		}
		return false;
	}
	
	public void load(Object[] loaded) {
		MapLoader loader = new MapLoader(loaded);
		int i=0;
		for(Iterator<String> itr=loader.getStrings();itr.hasNext();){
			String[] pair = itr.next().split(",");
			startingXPosition[i]=Float.parseFloat(pair[0]);
			startingYPosition[i]=Float.parseFloat(pair[1]);
			++i;
		}
		for(Square square:loader){
			addSquare(square);
		}
		
	}

	public String getStartingPosition(int i) {
		return startingXPosition[i]+","+startingYPosition[i];
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
