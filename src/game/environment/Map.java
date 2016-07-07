package game.environment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import game.Action;
import gui.graphics.GraphicEntity;
import main.Log;

public class Map extends GraphicEntity {

	private java.util.Map<String,SquareIdentity> squareIds = new HashMap<String,SquareIdentity>();

	private List<Square> allSquares = new ArrayList<Square>();
	private List<Square> blackSquares = new ArrayList<Square>();
	private List<Square> whiteSquares = new ArrayList<Square>();
	private List<FunctionalSquare> functionalSquares = new ArrayList<FunctionalSquare>();

	public Map() {
		super("blank");
		this.setVisible(false);
	}

	public List<FunctionalSquare> functionalSquares() {
		return functionalSquares;
	}

	public void addSquare(Square square){
		if(square.isFunctional()){
			functionalSquares.add((FunctionalSquare)square);
		}

		if(square.visibleToBlack()&&!square.visibleToWhite()){
			blackSquares.add(square);
		}
		else if(square.visibleToWhite()&&!square.visibleToBlack()){
			whiteSquares.add(square);
		}
		allSquares.add(square);
		addChild(square);
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

	public void load(Object[] loaded) {
		for(Square square:new MapLoader(loaded)){
			addSquare(square);
		}
	}

	private class MapLoader implements Iterable<Square>, Iterator<Square> {
		private int integerIndex = 3;
		private int floatIndex = 0;
		private int stringIndex = 0;
		private Object[] data;
		private Square currentSquare = null;

		private Iterator<Integer> integerIterator = new Iterator<Integer>(){
			@Override
			public boolean hasNext() {
				return integerIndex<floatIndex;
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
			floatIndex = (Integer)data[0];
			stringIndex = floatIndex+(Integer)data[1];
		}
		public void add(Square square){
			this.currentSquare = square;
		}
		@Override
		public boolean hasNext() {
			return stringIndex<data.length;
		}

		@Override
		public Square next() {
			String name = nextString();

			if(!squareIds.containsKey(name)){
				String half = name;
				List<Action> actions = new ArrayList<Action>(3);

				String[] split = half.split("X");
				for(int i=0;i<split.length;++i){
					if(!"void".equals(split[i])){
						int index = SquareAction.actionNames.indexOf(split[i]);
						if(index!=-1){							
							actions.add(SquareAction.actions.get(index));
						}
						else {
							index = UpdateAction.actionNames.indexOf(split[i]);
							if(index!=-1){
								actions.add(UpdateAction.actions.get(index));
							}
							else {
								Log.e("Map","COULD NOT FIND ACTION NAME:"+split[i]);
							}
						}
					}
					else actions.add(null);
				}
				SquareIdentity id = new SquareIdentity(name,actions.toArray(new Action[0]));
				squareIds.put(name,id);
			}

			return squareIds.get(name).create(getIntegers(), getFloats());
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

	public boolean isMallible() {
		return true;
	}

	public List<Square> getSquares() {
		return allSquares;
	}


}
