package game.environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import game.environment.oncreate.OnCreateSquare;
import game.environment.onstep.OnStepSquare;
import game.environment.onstep.WinStageOnStepAction;
import game.environment.update.UpdatableSquare;
import game.hero.Hero;
import game.modes.GameMode;
import game.modes.OverheadMode;
import game.modes.PlatformMode;
import game.modes.RaceMode;
import gui.Gui;
import gui.graphics.GraphicEntity;
import main.Hub;
import storage.Storage;

public class Map extends GraphicEntity {

	protected static final float gridSizeX = 100f;
	protected static final float gridSizeY = 100f;

	private List<Square> allSquares = new ArrayList<Square>();
	private List<OnStepSquare> functionalSquares = new ArrayList<OnStepSquare>();
	private List<UpdatableSquare> updateSquares = new ArrayList<UpdatableSquare>();
	private List<Creatable> onCreates = new ArrayList<Creatable>();
	private List<Square> templateSquares = new ArrayList<Square>();
	private List<Square> displaySquares = new ArrayList<Square>();

	private List<String> nextMaps = new ArrayList<String>();

	private java.util.Map<Square,List<OnStepSquare>> adjacentSquares = new HashMap<Square,List<OnStepSquare>>();

	private float[] startingXPosition = new float[2];
	private float[] startingYPosition = new float[2];

	private String name;
	private String filename;
	private int mapId=0;
	private int visibleColour=0;

	private Map() {
		super("blank");
		this.setVisible(false);
	}

	public String getName(){
		return name;
	}
	public String getFileName() {
		return filename;
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
			((UpdatableSquare)square).display();
		}
		if(square instanceof Creatable){
			onCreates.add((Creatable) square);
		}
		displaySquares.add(square);
		square.displayFor(visibleColour);
	}
	public void unDisplaySquare(Square square) {
		if(square==null)return;
		if(square.isFunctional()){
			functionalSquares.remove((OnStepSquare)square);
		}
		if(square instanceof UpdatableSquare){
			updateSquares.remove((UpdatableSquare)square);
			((UpdatableSquare)square).undisplay();
		}
		if(square instanceof Creatable){
			onCreates.remove((Creatable) square);
		}
		displaySquares.remove(square);
	}

	public void setVisibleSquares(int colour){
		for(Square square:displaySquares){
			square.displayFor(colour);
		}
		visibleColour = colour;
	}
	public int getVisibleColour() {
		return visibleColour;
	}

	private float xOffset = 0f;
	private float yOffset = 0f;
	private boolean lightDependency = false;
	@Override
	public void reposition(float x, float y){
		xOffset = x-getX();
		yOffset = y-getY();
		super.reposition(x,y);
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
		for(Creatable square:onCreates){
			square.create();
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
			if(target.isCompletelyWithin(square)&&square.getOnHitAction(accordingTo).isPassible()){			
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
		if(id==0){
			return createMap(-60);
		}

		Map map = new Map();
		map.setMapId(id);
		return map;

	}

	public String getNextMap(Integer target) {
		if(target>=0&&target<nextMaps.size()){
			return nextMaps.get(target);
		}
		return null;
	}
	public Integer setNextMap(String name){
		if(!nextMaps.contains(name)){
			nextMaps.add(name);
		}
		return nextMaps.indexOf(name);
	}
	public void saveTo(List<Object> toSave) {
		toSave.add(getMapId());
		toSave.add(lightDependency?0:1);
		if(Storage.debug)System.out.println();
		toSave.add(getStartingXPosition(0));
		toSave.add(getStartingYPosition(0));
		toSave.add(getStartingXPosition(1));
		toSave.add(getStartingYPosition(1));
		if(Storage.debug)System.out.println();
		for(String name:nextMaps){
			toSave.add(name);
		}
		toSave.add(getTemplateSquares().size());
		for(Square square:getTemplateSquares()){
			square.saveTo(toSave);
		}
		if(Storage.debug)System.out.println();
		for(Square square:getSquares()){
			square.saveTo(toSave);
		}
	}

	public static void load(String name, String fileName, Object[] loaded) {
		if(Hub.map!=null){
			if(name.equals("Restart")){
				name = Hub.map.name;
			}
			if("Restart".equals(fileName)){
				fileName = Hub.map.filename;
			}
		}
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
		Hub.map.name = name;
		Hub.map.filename = fileName;
		Hub.map.lightDependency = loader.nextInteger()==0;
		for(Iterator<String> names=loader.getStrings();names.hasNext();Hub.map.nextMaps.add(names.next())){			
		}
		int i=0;
		for(Iterator<Float> itr=loader.getFloats();i<2;++i){
			Hub.map.startingXPosition[i]=itr.next();
			Hub.map.startingYPosition[i]=itr.next();
		}
		int size = loader.nextInteger();
		for(i=0;i<size;++i){
			Square toAdd = Square.create(loader.getIntegers(), loader.getFloats());
			Hub.map.templateSquares.add(toAdd);
		}
		for(Square square:loader){
			Hub.map.addSquare(square);
		}		
	}

	public void moveToStart(Hero hero){
		if(hero.isBlack()){
			hero.reposition(startingXPosition[0],startingYPosition[0]);
		}
		else if(hero.isWhite()){
			hero.reposition(startingXPosition[1],startingYPosition[1]);
		}
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
	public static final boolean X_axis = true;
	public static final boolean Y_axis = false;
	public float getFloatCoordinate(int value,boolean axis){
		if(axis==X_axis){
			return ((float)value)/gridSizeX;
		}
		else if(axis==Y_axis){
			return ((float)value)/gridSizeY;
		}
		else {
			return 0.5f;
		}
	}
	public int getIntCoordinate(float value, boolean axis){
		if(axis==X_axis){
			if(value<=0){
				return (int) (value*gridSizeX-0.5f);
			}
			else {
				return (int) (value*gridSizeX+0.5f);
			}
		}
		else if(axis==Y_axis){
			if(value<=0){
				return (int) (value*gridSizeY-0.5f);
			}
			else {
				return (int) (value*gridSizeY+0.5f);
			}
		}
		else {
			return (int) (gridSizeX/2);
		}
	}

	public int getMapId(){
		return mapId;
	}
	public void setMapId(int id){
		this.mapId = id;
	}


	public boolean isLightDependent() {
		return lightDependency ;
	}
	public void setLightDependency(boolean dependency){
		this.lightDependency = dependency;
	}
	public GameMode getGameMode() {
		switch((mapId+20)/-20){
		case 0:{
			return new OverheadMode();
		}
		case 1:{
			return new PlatformMode();
		}
		case 2:{
			return new RaceMode();
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
				return floatIndex+1<stringIndex;
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

	public static void main(String[] args){
		File file = userSave("maps");
		while(file!=null){
			Storage.loadMap(file.getAbsolutePath());
			game.environment.Map map = game.environment.Map.createMap(Hub.map.getMapId());
			Hub.map.copyTo(map);
			Storage.saveMap(file.getAbsolutePath(), map);
			file = userSave("maps");
		}
	}

	public static File userSave(String sub){
		JFileChooser  fc = new JFileChooser("data"+File.separator+sub);
		int returnVal = fc.showOpenDialog(new JPanel());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else {
			return null;
		}
	}

	public void copyTo(Map map) {
		for(Square square:Hub.map.allSquares){
			map.addSquare(square);
			if(square instanceof OnStepSquare){
				for(SquareAction action:square.getActions()){
					if(action instanceof WinStageOnStepAction){
						WinStageOnStepAction wsosa = (WinStageOnStepAction)action;
						int index = map.nextMaps.indexOf(wsosa.getTarget());
						if(index==-1){
							map.nextMaps.add(nextMaps.get(wsosa.getTarget()));
							wsosa.setTarget(map.nextMaps.size()-1);
						}
						else {
							wsosa.setTarget(index);
						}
					}
				}
			}
		}
		for(Square square:Hub.map.getTemplateSquares()){
			map.addTemplateSquare(square);
			if(square instanceof OnStepSquare){
				for(SquareAction action:square.getActions()){
					if(action instanceof WinStageOnStepAction){
						WinStageOnStepAction wsosa = (WinStageOnStepAction)action;
						if(!map.nextMaps.contains(nextMaps.get(wsosa.getTarget()))){
							map.nextMaps.add(nextMaps.get(wsosa.getTarget()));
						}
						wsosa.setTarget(map.nextMaps.indexOf(wsosa.getTarget()));
					}
				}
			}
		}
		map.setStartPosition(0, getStartingXPosition(0), getStartingYPosition(0));
		map.setStartPosition(1, getStartingXPosition(1), getStartingYPosition(1));
		map.setLightDependency(this.isLightDependent());
	}


}
