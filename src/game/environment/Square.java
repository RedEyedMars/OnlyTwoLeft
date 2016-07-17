package game.environment;

import gui.graphics.GraphicEntity;
import main.Hub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import game.Action;
import game.Hero;
public class Square extends GraphicEntity{

	public static final byte green = 0;
	public static final byte darkGreen = 1;
	public static final byte blue = 2;
	public static final byte darkBlue = 3;
	public static final byte red = 4;
	public static final byte darkRed = 5;
	public static final byte black = 6;
	public static final byte white = 7;

	private int visibleTo = 0;//0=both,1=black,2=white
	private int colour;
	protected int actionType = 0;
	public Square(int colour, int visibleTo,int bufferSize, Iterator<Integer> ints, Iterator<Float> floats) {
		super("squares");
		this.colour = colour;
		this.setFrame(colour);
		this.visibleTo = visibleTo;
		bufferSize = bufferSize;
		if(bufferSize==1){
			float size = floats.next();
			adjust(size,size);
		}
		else if(bufferSize==2){
			float width = floats.next();
			float height = floats.next();
			adjust(width,height);
		}
		else if(bufferSize==3){
			float x = floats.next();
			float y = floats.next();
			float size = floats.next();
			setX(x);setY(y);
			adjust(size,size);
		}
		else if(bufferSize==4){
			float x = floats.next();
			float y = floats.next();
			float width = floats.next();
			float height = floats.next();
			setX(x);setY(y);
			adjust(width,height);
		}
	}
	public Square(int colour, int bufferSize, Iterator<Float> floats) {
		this(colour,0,bufferSize,new Iterator<Integer>(){
			public boolean hasNext() {
				return false;
			}
			@Override
			public Integer next() {
				return null;
			}},floats);
	}

	public Square(int colour,int visibleTo, float width, float height) {
		super("squares");
		adjust(width,height);
		this.setFrame(colour);
		this.visibleTo = visibleTo;
	}
	public Square(int colour,float width, float height) {
		this(colour,0,width,height);
	}
	public Square(int colour,int visibleTo, float size) {
		this(colour,visibleTo,size,size);
	}
	public Square(int colour,float size) {
		this(colour,0,size,size);
	}
	public boolean isFunctional() {
		return false;
	}
	public boolean visibleToBlack() {
		return visibleTo<2;
	}
	public boolean visibleToWhite() {
		return visibleTo!=1&&visibleTo!=3;
	}


	@Override
	public void adjust(float x, float y){
		this.getGraphicElement().adjust(x, y);
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
		return getChild(index).getX()+xOffset-getX();
	}
	@Override
	public float offsetY(int index){
		return getChild(index).getY()+yOffset-getY();
	}

	public void saveTo(List<Object> toSave) {
		toSave.add(getX());
		toSave.add(getY());
		int floats = 2;
		if(getWidth()==getHeight()){
			toSave.add(getWidth());
			floats = 3;
		}
		else {
			toSave.add(getWidth());
			toSave.add(getHeight());
			floats = 4;
		}
		toSave.add(actionType);
		toSave.add(this.textureIndex());
		toSave.add(visibleTo);
		toSave.add(floats);	
		for(Action action:getActions()){
			action.saveTo(toSave);
		}
	}
	public List<Action> getActions() {
		return new ArrayList<Action>();
	}
	public static Square create(Iterator<Integer> ints, Iterator<Float> floats){		
		Square square = null;
		int actionType = ints.next();
		int colour = ints.next();
		if(!ints.hasNext())return null;
		int visibleTo = ints.next();
		int bufferSize = ints.next();
		if(actionType==0){
			square = new Square(colour,visibleTo,bufferSize,ints,floats);
		}
		else if(actionType==1){
			SquareAction action = SquareAction.getAction(ints.next());
			square = new FunctionalSquare(colour,visibleTo,bufferSize,ints,floats,action);
		}
		else if(actionType==2){
			SquareAction action1 = SquareAction.getAction(ints.next());
			SquareAction action2 = SquareAction.getAction(ints.next());
			square = new FunctionalSquare(colour,visibleTo,bufferSize,ints,floats,action1,action2);
		}
		else if(actionType==3){
			UpdateAction action = UpdateAction.getAction(ints.next());
			square = new UpdatableSquare(colour,visibleTo,bufferSize,ints,floats,null,action);
		}
		else if(actionType==4){
			SquareAction action1 = SquareAction.getAction(ints.next());
			UpdateAction action2 = UpdateAction.getAction(ints.next());
			square = new UpdatableSquare(colour,visibleTo,bufferSize,ints,floats,action1,action2);
		}
		else if(actionType==5){
			SquareAction action1 = SquareAction.getAction(ints.next());
			SquareAction action2 = SquareAction.getAction(ints.next());
			UpdateAction action3 = UpdateAction.getAction(ints.next());
			square = new UpdatableSquare(colour,visibleTo,bufferSize,ints,floats,action1,action2,action3);
		}
		else if(actionType==6){
			square = new OnCreateSquare(colour,visibleTo,bufferSize,ints,floats);
		}
		return square;
	}
	public static Iterator<Integer> makeInts(int squareAction1, int squareAction2, int updateAction, boolean onCreateAction,
			int colour, int visibleTo,int bufferSize) {		
		List<Integer> ints = new ArrayList<Integer>();
		if(!onCreateAction){
			if(squareAction1==-1&&squareAction2==-1&&updateAction==-1){
				ints.add(0);
				ints.add(colour);
				ints.add(visibleTo);
				ints.add(bufferSize);
			}
			else if(squareAction1>=0&&updateAction==-1){
				if(squareAction2==-1||squareAction1==squareAction2){
					ints.add(1);
					ints.add(colour);
					ints.add(visibleTo);
					ints.add(bufferSize);
					ints.add(squareAction1);
				}
				else {
					ints.add(2);
					ints.add(colour);
					ints.add(visibleTo);
					ints.add(bufferSize);
					ints.add(squareAction1);
					ints.add(squareAction2);
				}
			}
			else if(updateAction>=0){
				if(squareAction1==-1&&squareAction2==-1){
					ints.add(3);
					ints.add(colour);
					ints.add(visibleTo);
					ints.add(bufferSize);
					ints.add(updateAction);
				}
				else if(squareAction2==-1||squareAction1==squareAction2){
					ints.add(4);
					ints.add(colour);
					ints.add(visibleTo);
					ints.add(bufferSize);
					ints.add(squareAction1);
					ints.add(updateAction);
				}
				else {
					ints.add(5);
					ints.add(colour);
					ints.add(visibleTo);
					ints.add(bufferSize);
					ints.add(squareAction1);
					ints.add(squareAction2);
					ints.add(updateAction);
				}
			}
		}
		else{
			ints.add(6);
			ints.add(colour);
			ints.add(visibleTo);
			ints.add(bufferSize);
			ints.add(0);
		}

		int lastUpdatableSquare=Hub.map.getSquares().size()-1;
		for(;lastUpdatableSquare>=0;--lastUpdatableSquare){
			if(Hub.map.getSquares().get(lastUpdatableSquare) instanceof UpdatableSquare){
				break;
			}
		}
		if(squareAction1!=-1&&SquareAction.getAction(squareAction1).numberOfTargets()==1){			
			ints.add(lastUpdatableSquare);
		}
		if(squareAction1!=squareAction2&&squareAction2!=-1&&SquareAction.getAction(squareAction2).numberOfTargets()==1){
			ints.add(lastUpdatableSquare);
		}
		return ints.iterator();
	}
}
