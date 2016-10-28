package com.rem.otl.core.game.environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rem.otl.core.editor.Editor;
import com.rem.otl.core.game.environment.oncreate.OnCreateSquare;
import com.rem.otl.core.game.environment.onstep.OnStepAction;
import com.rem.otl.core.game.environment.onstep.OnStepSquare;
import com.rem.otl.core.game.environment.program.ProgrammableSquare;
import com.rem.otl.core.game.environment.update.UpdatableSquare;
import com.rem.otl.core.game.environment.update.UpdateAction;
import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.game.menu.GetFileMenu;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.main.Hub;
import com.rem.otl.core.storage.Storage;
public class Square extends GraphicEntity implements Colourable, Saveable{

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
	private int shapeType;
	public Square(int actionType, int shapeType, int blackColour,int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		super("squares");
		this.blackColour = blackColour%16;
		this.whiteColour = whiteColour%16;
		this.displayFor(Hero.BOTH_INT);
		this.shapeType = shapeType;
		this.setShape(shapeType);
		reposition(Hub.map.getFloatCoordinate(ints.next(),Map.X_axis),
				   Hub.map.getFloatCoordinate(ints.next(),Map.Y_axis));
		resize(Hub.map.getFloatCoordinate(ints.next(),Map.X_axis),
				Hub.map.getFloatCoordinate(ints.next(),Map.Y_axis));
		this.actionType = actionType;
		loadActions(ints,floats);
	}
	public Square(int shapeType, int blackColour,int whiteColour, Iterator<Integer> ints, Iterator<Float> floats) {
		this(0,shapeType,blackColour,whiteColour,ints,floats);
	}
	protected void loadActions(Iterator<Integer> ints, Iterator<Float> floats) {
	}
	public Square(int colour, float width, float height){
		this(colour,colour,width,height);
	}
	public Square(int blackColour, int whiteColour, float width, float height) {
		super("squares");
		resize(width,height);
		this.blackColour = blackColour;
		this.whiteColour = whiteColour;
		displayFor(Hero.BOTH_INT);
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

	public void showChildren() {
		for(GraphicEntity child:children){
			if(child instanceof Square){
				((Square)child).showChildren();
			}
			else {
				child.setVisible(true);
			}
		}
	}
	public void hideChildren() {
		for(GraphicEntity child:children){
			if(child instanceof Square){
				((Square)child).hideChildren();
			}
			else {
				child.setVisible(false);
			}
		}
	}
	public void displayFor(int colour){
		if(!visibleToBlack()&&!visibleToWhite()){
			turnOff();
		}
		else if(colour==Hero.BOTH_INT){
			if(visibleToBlack()){
				turnOn();
				setFrame(blackColour);
			}
			else if(visibleToWhite()){
				turnOn();
				setFrame(whiteColour);
			}
			else turnOff();
		}
		else if(colour==Hero.BLACK_INT){
			if(visibleToBlack()){
				turnOn();
				setFrame(blackColour);
			}
			else turnOff();
		}
		else if(colour==Hero.WHITE_INT){
			if(visibleToWhite()){
				turnOn();
				setFrame(whiteColour);
			}
			else turnOff();
		}
		else turnOff();
	}
	public int getColour(int colour) {
		if(colour==Hero.BLACK_INT){
			return blackColour;
		}
		else if(colour==Hero.WHITE_INT){
			return whiteColour;
		}
		else return blackColour;
	}
	@Override
	public void resize(float x, float y){
		this.getGraphicElement().resize(x, y);
	}

	private float xOffset = 0f;
	private float yOffset = 0f;
	public void move(Float x, Float y){
		reposition(getX()+x,getY()+y);
	}
	@Override
	public void reposition(float x, float y){
		xOffset = x-getX();
		yOffset = y-getY();
		super.reposition(x,y);
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
		toSave.add(shapeType);
		toSave.add(blackColour);
		toSave.add(whiteColour);
		toSave.add(Hub.map.getIntCoordinate(getX(),Map.X_axis));
		toSave.add(Hub.map.getIntCoordinate(getY(),Map.Y_axis));
		toSave.add(Hub.map.getIntCoordinate(getWidth(),Map.X_axis));
		toSave.add(Hub.map.getIntCoordinate(getHeight(),Map.Y_axis));
		saveActions(toSave);
		
		if(Storage.debug_save)System.out.println();
	}
	public List<SquareAction> getActions() {
		return new ArrayList<SquareAction>();
	}
	protected void saveActions(List<Object> toSave){
		for(SquareAction action:getActions()){
			if(action!=null){
				action.saveTo(toSave);
			}
			else {
				toSave.add(-1);
			}
		}
	}

	public int getReflectTriangle(int colour) {
		//later we can do "only reflect if the colour is right" right now just reflects everything
		return entity.getReflectedShape();
	}
	public int saveType(){
		return 2;
	}
	public static Square create(Iterator<Integer> ints, Iterator<Float> floats){
		Square square = null;

		if(Storage.debug_load)Hub.log.bufferDebug("Square.create", "square{(");
		int actionType = ints.next();
		if(Storage.debug_load)Hub.log.bufferDebug("Square.create", ")");
		int shapeType = ints.next();
		int blackColour = ints.next();
		int whiteColour = ints.next();

		if(actionType==0){
			square = new Square(shapeType,blackColour,whiteColour,ints,floats);
		}
		else if(actionType>=1&&actionType<=2){
			square = new OnStepSquare(actionType,shapeType,blackColour,whiteColour,ints,floats);
		}
		else if(actionType>=3&&actionType<=5){	
			square = new UpdatableSquare(actionType,shapeType,blackColour,whiteColour,ints,floats);
		}
		else if(actionType==6){
			square = new OnCreateSquare(shapeType,blackColour,whiteColour,ints,floats);
		}
		else if(actionType==7){
			square = new ProgrammableSquare(shapeType,blackColour,whiteColour,ints,floats);
		}
		if(Storage.debug_load)Hub.log.debug("Square.create", "}");
		return square;
	}
	public static Iterator<Integer> makeInts(Editor editor,
			int squareAction1, int squareAction2, List<Integer> updateAction, boolean onCreateAction, boolean programAction,
			int shapeType, int colour, int colour2,float x, float y, int w, int h) {		
		List<Integer> ints = new ArrayList<Integer>();
		if(!onCreateAction&&!programAction){
			if(squareAction1==-1&&squareAction2==-1&&updateAction.size()==0){
				ints.add(0);
				ints.add(shapeType);
				ints.add(colour);
				ints.add(colour2);
				ints.add(Hub.map.getIntCoordinate(x, Map.X_axis));
				ints.add(Hub.map.getIntCoordinate(y, Map.Y_axis));
				ints.add(w);
				ints.add(h);
			}
			else if((squareAction1!=-1||squareAction2!=-1)&&updateAction.size()==0){
				if(squareAction1==squareAction2){
					ints.add(1);
					ints.add(shapeType);
					ints.add(colour);
					ints.add(colour2);
					ints.add(Hub.map.getIntCoordinate(x, Map.X_axis));
					ints.add(Hub.map.getIntCoordinate(y, Map.Y_axis));
					ints.add(w);
					ints.add(h);
					ints.add(squareAction1);
					addMapNameToInts(editor,ints,squareAction1);
				}
				else {
					ints.add(2);
					ints.add(shapeType);
					ints.add(colour);
					ints.add(colour2);
					ints.add(Hub.map.getIntCoordinate(x, Map.X_axis));
					ints.add(Hub.map.getIntCoordinate(y, Map.Y_axis));
					ints.add(w);
					ints.add(h);
					ints.add(squareAction1);
					addMapNameToInts(editor,ints,squareAction1);
					ints.add(squareAction2);
					addMapNameToInts(editor,ints,squareAction2);
				}
			}
			else if(updateAction.size()>0){
				if(squareAction1==-1&&squareAction2==-1){
					ints.add(3);										
				}				
				else if(squareAction1==squareAction2){
					ints.add(4);
				}
				else {
					ints.add(5);
				}
				ints.add(shapeType);
				ints.add(colour);
				ints.add(colour2);
				ints.add(Hub.map.getIntCoordinate(x, Map.X_axis));
				ints.add(Hub.map.getIntCoordinate(y, Map.Y_axis));
				ints.add(w);
				ints.add(h);
				if(squareAction1==-1&&squareAction2==-1){								
				}				
				else if(squareAction1==squareAction2){
					ints.add(squareAction1);
					addMapNameToInts(editor,ints,squareAction1);
				}
				else {
					ints.add(squareAction1);
					addMapNameToInts(editor,ints,squareAction1);
					ints.add(squareAction2);
					addMapNameToInts(editor,ints,squareAction2);
				}
				if(updateAction.size()==1){
					ints.add(updateAction.get(0));
					ints.add(UpdateAction.getAction(updateAction.get(0)).getInt(UpdateAction.DEFAULT_STATE));
					ints.add(-1);
				}
				else {
					ints.add(-2);
					ints.add(updateAction.size());
					for(Integer ua:updateAction){
						ints.add(ua);
						ints.add(UpdateAction.getAction(ua).getInt(UpdateAction.DEFAULT_STATE));
						ints.add(-1);
					}
				}
				ints.add(0);//The size of dependants, 0 because no depends have been assigned yet
			}
		}
		else if(onCreateAction){
			ints.add(6);
			ints.add(shapeType);
			ints.add(colour);
			ints.add(colour);
			ints.add(Hub.map.getIntCoordinate(x, Map.X_axis));
			ints.add(Hub.map.getIntCoordinate(y, Map.Y_axis));
			ints.add(w);
			ints.add(h);
			ints.add(0);
		}
		else if(programAction){
			ints.add(7);
			ints.add(shapeType);
			ints.add(colour);
			ints.add(colour);
			ints.add(Hub.map.getIntCoordinate(x, Map.X_axis));
			ints.add(Hub.map.getIntCoordinate(y, Map.Y_axis));
			ints.add(w);
			ints.add(h);/*
			ints.add(-1);
			ints.add(-1);
			ints.add(-1);
			ints.add(0);
			ints.add(-1);*/
			//
			ints.add(1);//number of states
			ints.add(0);//condition
			ints.add(-1);//next condition
			ints.add(0);//actions
			ints.add(-1);//states
			//
			ints.add(0);//dependants
		}


		return ints.iterator();
	}
	private static void addMapNameToInts(Editor editor, List<Integer> ints, int squareAction) {
		if(OnStepAction.getAction(squareAction)==null)return;
		if(OnStepAction.getAction(squareAction).targetType()==2){
			ints.add(-2);
		}
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

	@Override
	public Boolean[] getColours(boolean isBlack) {	

		int texture = isBlack?blackColour:whiteColour;
		for(Integer forbid:new Integer[]{-1,1,3,5,6,7,9,11,13}){
			if(texture==forbid)return null;
		}
		boolean myRed=texture==4||texture==8||texture==10||texture==15;
		boolean myGreen=texture==0||texture==8||texture==12||texture==15;
		boolean myBlue=texture==2||texture==10||texture==12||texture==15;

		return new Boolean[]{myRed, myGreen, myBlue};
	}

	@Override
	public void setColour(boolean red, boolean green, boolean blue) {
	}

	public void changeColour(Integer blackColour, Integer whiteColour) {
		if(blackColour!=null){
			this.blackColour=blackColour;
		}
		if(whiteColour!=null){
			this.whiteColour=whiteColour;
		}
		displayFor(Hub.map.getVisibleColour());
	}

	
}
