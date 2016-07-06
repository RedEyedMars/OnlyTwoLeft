package game.environment;

import gui.graphics.GraphicEntity;

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
	private SquareIdentity id;
	public Square(SquareIdentity id,int visibleTo, int bufferSize, Iterator<Float> floats) {
		super("squares");
		this.id = id;
		this.setFrame(id.colour());
		this.visibleTo = visibleTo;
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
	public Square(SquareIdentity id, int bufferSize, Iterator<Float> floats) {
		this(id,0,bufferSize,floats);
	}
	
	public Square(SquareIdentity id,int visibleTo, float width, float height) {
		super("squares");
		adjust(width,height);
		this.setFrame(id.colour());
		this.visibleTo = visibleTo;
	}
	public Square(SquareIdentity id,float width, float height) {
		this(id,0,width,height);
	}
	public Square(SquareIdentity id,int visibleTo, float size) {
		this(id,visibleTo,size,size);
	}
	public Square(SquareIdentity id,float size) {
		this(id,0,size,size);
	}
	public boolean isFunctional() {
		return false;
	}
	public boolean visibleToBlack() {
		return visibleTo<2;
	}
	public boolean visibleToWhite() {
		return visibleTo!=1;
	}
	public void saveTo(List<Object> toSave) {
		toSave.add(this.textureIndex());
		toSave.add(visibleTo);
		int floats = 0;
		toSave.add(getX());
		toSave.add(getY());
		if(getWidth()==getHeight()){
			toSave.add(getWidth());
			floats = 3;
		}
		else {
			toSave.add(getWidth());
			toSave.add(getHeight());
			floats = 4;
		}
		toSave.add(floats);
		id.saveTo(toSave);		
	}

	public SquareIdentity getIdentity() {
		return id;
	}
	
	@Override
	public void adjust(float x, float y){
		this.getGraphicElement().adjust(x, y);
	}
}
