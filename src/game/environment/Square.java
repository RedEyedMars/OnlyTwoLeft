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

	private int blackColour=-1;
	private int whiteColour=-1;
	protected int actionType = 0;
	public Square(int blackColour,int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super("squares");
		this.blackColour = blackColour;
		this.whiteColour = whiteColour;
		this.displayFor(0);/*
		setX(floats.next());
		setY(floats.next());
		if(ints.next()==3){//bufferSize
			float size = floats.next();
			adjust(size,size);
		}
		else {
			float w = floats.next();
			float h = floats.next();
			adjust(w,h);		
		}*/
		setX(Hub.map.getRealX(ints.next()));
		setY(Hub.map.getRealY(ints.next()));
		float w = Hub.map.getRealX(ints.next());		
		float h = Hub.map.getRealY(ints.next());
		adjust(w,h);
	}

	public Square(int colour, float width, float height){
		this(colour,colour,width,height);
	}
	public Square(int blackColour, int whiteColour, float width, float height) {
		super("squares");
		adjust(width,height);
		this.blackColour = blackColour;
		this.whiteColour = whiteColour;
		displayFor(0);
	}
	public boolean isFunctional() {
		return false;
	}
	public boolean visibleToBlack() {
		return blackColour>=0;
	}
	public boolean visibleToWhite() {
		return whiteColour>=0;
	}
	public void displayFor(int colour){
		if(blackColour==-1&&whiteColour==-1){
			turnOff();
			for(GraphicEntity child:children){
				if(child instanceof Square){
					((Square)child).displayFor(colour);
				}
			}
		}
		else if(colour==0){
			if(blackColour>=0){
				turnOn();
				setFrame(blackColour);
			}
			else if(whiteColour>=0){
				turnOn();
				setFrame(whiteColour);
			}
			else turnOff();
		}
		else if(colour==1){
			if(blackColour>=0){
				turnOn();
				setFrame(blackColour);
			}
			else turnOff();
		}
		else if(colour==2){
			if(whiteColour>=0){
				turnOn();
				setFrame(whiteColour);
			}
			else turnOff();
		}
		else turnOff();
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
	public boolean isWithin(GraphicEntity target){
		return isWithin(target.getX()+0.0001f, target.getY()+0.0001f)||
				isWithin(target.getX()-0.0001f+target.getWidth(), target.getY()+0.0001f)||
				isWithin(target.getX()+0.0001f, target.getY()-0.0001f+target.getHeight())||
				isWithin(target.getX()-0.0001f+target.getWidth(), target.getY()-0.0001f+target.getHeight());
	}
	public boolean isCompletelyWithin(Square q) {
		return (getX()>=q.getX()&&getX()+getWidth()<=q.getX()+q.getWidth()&&
				getY()>=q.getY()&&getY()+getHeight()<=q.getY()+q.getHeight());
	}
	public void saveTo(List<Object> toSave) {
		toSave.add(actionType);
		toSave.add(blackColour);
		toSave.add(whiteColour);
		toSave.add(Hub.map.getIntXLow(getX()));
		toSave.add(Hub.map.getIntYLow(getY()));
		toSave.add(Hub.map.getIntXHigh(getWidth()));
		toSave.add(Hub.map.getIntYHigh(getHeight()));
		for(SquareAction action:getActions()){
			action.saveTo(toSave);
		}
	}
	public List<SquareAction> getActions() {
		return new ArrayList<SquareAction>();
	}
	public static Square create(Iterator<Integer> ints, Iterator<Float> floats){		
		Square square = null;
		int actionType = ints.next();
		int blackColour = ints.next();
		int whiteColour = ints.next();
		if(actionType==0){
			square = new Square(blackColour,whiteColour,ints,floats);
		}
		else if(actionType>=1&&actionType<=2){
			square = new OnStepSquare(blackColour,whiteColour,ints,floats,actionType);
		}
		else if(actionType>=3&&actionType<=5){	
			square = new UpdatableSquare(blackColour,whiteColour,ints,floats,actionType);
		}
		else if(actionType==6){
			square = new OnCreateSquare(blackColour,whiteColour,ints,floats);
		}
		return square;
	}
	public static Iterator<Integer> makeInts(
			int squareAction1, int squareAction2, int updateAction, boolean onCreateAction,
			int colour, int colour2,int x, int y, int w, int h) {		
		List<Integer> ints = new ArrayList<Integer>();
		if(!onCreateAction){
			if(squareAction1==-1&&squareAction2==-1&&updateAction==-1){
				ints.add(0);
				ints.add(colour);
				ints.add(colour2);
				ints.add(x);
				ints.add(y);
				ints.add(w);
				ints.add(h);
			}
			else if(squareAction1>=0&&updateAction==-1){
				if(squareAction2==-1||squareAction1==squareAction2){
					ints.add(1);
					ints.add(colour);
					ints.add(colour2);
					ints.add(x);
					ints.add(y);
					ints.add(w);
					ints.add(h);
					ints.add(squareAction1);
				}
				else {
					ints.add(2);
					ints.add(colour);
					ints.add(colour2);
					ints.add(x);
					ints.add(y);
					ints.add(w);
					ints.add(h);
					ints.add(squareAction1);
					ints.add(squareAction2);
				}
			}
			else if(updateAction>=0){
				if(squareAction1==-1&&squareAction2==-1){
					ints.add(3);
					ints.add(colour);
					ints.add(colour2);
					ints.add(x);
					ints.add(y);
					ints.add(w);
					ints.add(h);
					ints.add(updateAction);
				}
				else if(squareAction2==-1||squareAction1==squareAction2){
					ints.add(4);
					ints.add(colour);
					ints.add(colour2);
					ints.add(x);
					ints.add(y);
					ints.add(w);
					ints.add(h);
					ints.add(squareAction1);
					ints.add(updateAction);
				}
				else {
					ints.add(5);
					ints.add(colour);
					ints.add(colour2);
					ints.add(x);
					ints.add(y);
					ints.add(w);
					ints.add(h);
					ints.add(squareAction1);
					ints.add(squareAction2);
					ints.add(updateAction);
				}
			}
		}
		else{
			ints.add(6);
			ints.add(colour);
			ints.add(colour);
			ints.add(x);
			ints.add(y);
			ints.add(w);
			ints.add(h);
			ints.add(0);
		}

		if(updateAction!=-1){
			ints.add(UpdateAction.getAction(updateAction).getDefaultState()?1:0);
			ints.add(-1);
			ints.add(0);
		}
		return ints.iterator();
	}

	public static void addArgsFromSquare(Square square, final List<Integer> ints, final List<Float> floats){
		List<Object> probe = new ArrayList<Object>(){
			@Override
			public boolean add(Object obj){
				if(obj instanceof Integer){
					return ints.add((Integer) obj);
				}
				else if(obj instanceof Float){
					return floats.add((Float) obj);
				}
				return false;
			}
		};
		square.saveTo(probe);
	}
	public static Square copy(Square square) {
		List<Integer> ints = new ArrayList<Integer>();
		List<Float> floats = new ArrayList<Float>();
		Square.addArgsFromSquare(square, ints, floats);
		return Square.create(ints.iterator(), floats.iterator());
	}
}
