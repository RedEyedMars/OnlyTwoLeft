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
	public Square(int blackColour,int whiteColour,int bufferSize, Iterator<Integer> ints, Iterator<Float> floats) {
		super("squares");
		this.blackColour = blackColour;
		this.whiteColour = whiteColour;
		this.displayFor(0);
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
		if(colour==0){
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

	public boolean isCompletelyWithin(Square q) {
		double x = getX()+getWidth();
		double y = getY()+getHeight();
		return (getX()>=q.getX()&&x<=q.getX()+q.getWidth()&&
				getY()>=q.getY()&&y<=q.getY()+q.getHeight());
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
		toSave.add(blackColour);
		toSave.add(whiteColour);
		toSave.add(floats);	
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
		int bufferSize = ints.next();
		if(actionType==0){
			square = new Square(blackColour,whiteColour,bufferSize,ints,floats);
		}
		else if(actionType==1){
			OnStepAction action = OnStepAction.getAction(ints.next());
			square = new OnStepSquare(blackColour,whiteColour,bufferSize,ints,floats,action);
		}
		else if(actionType==2){
			OnStepAction action1 = OnStepAction.getAction(ints.next());
			OnStepAction action2 = OnStepAction.getAction(ints.next());
			square = new OnStepSquare(blackColour,whiteColour,bufferSize,ints,floats,action1,action2);
		}
		else if(actionType==3){
			UpdateAction action = UpdateAction.getAction(ints.next());
			if(action==null)return null;
			square = new UpdatableSquare(blackColour,whiteColour,bufferSize,ints,floats,null,action);
		}
		else if(actionType==4){
			OnStepAction action1 = OnStepAction.getAction(ints.next());
			UpdateAction action2 = UpdateAction.getAction(ints.next());
			square = new UpdatableSquare(blackColour,whiteColour,bufferSize,ints,floats,action1,action2);
		}
		else if(actionType==5){
			OnStepAction action1 = OnStepAction.getAction(ints.next());
			OnStepAction action2 = OnStepAction.getAction(ints.next());
			UpdateAction action3 = UpdateAction.getAction(ints.next());
			square = new UpdatableSquare(blackColour,whiteColour,bufferSize,ints,floats,action1,action2,action3);
		}
		else if(actionType==6){
			square = new OnCreateSquare(blackColour,whiteColour,bufferSize,ints,floats);
		}
		return square;
	}
	public static Iterator<Integer> makeInts(int squareAction1, int squareAction2, int updateAction, boolean onCreateAction,
			int colour, int colour2,int bufferSize) {		
		List<Integer> ints = new ArrayList<Integer>();
		if(!onCreateAction){
			if(squareAction1==-1&&squareAction2==-1&&updateAction==-1){
				ints.add(0);
				ints.add(colour);
				ints.add(colour2);
				ints.add(bufferSize);
			}
			else if(squareAction1>=0&&updateAction==-1){
				if(squareAction2==-1||squareAction1==squareAction2){
					ints.add(1);
					ints.add(colour);
					ints.add(colour2);
					ints.add(bufferSize);
					ints.add(squareAction1);
				}
				else {
					ints.add(2);
					ints.add(colour);
					ints.add(colour2);
					ints.add(bufferSize);
					ints.add(squareAction1);
					ints.add(squareAction2);
				}
			}
			else if(updateAction>=0){
				if(squareAction1==-1&&squareAction2==-1){
					ints.add(3);
					ints.add(colour);
					ints.add(colour2);
					ints.add(bufferSize);
					ints.add(updateAction);
				}
				else if(squareAction2==-1||squareAction1==squareAction2){
					ints.add(4);
					ints.add(colour);
					ints.add(colour2);
					ints.add(bufferSize);
					ints.add(squareAction1);
					ints.add(updateAction);
				}
				else {
					ints.add(5);
					ints.add(colour);
					ints.add(colour2);
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
			ints.add(colour);
			ints.add(bufferSize);
			ints.add(0);
		}
		
		if(updateAction!=-1){
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
