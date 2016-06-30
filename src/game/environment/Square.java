package game.environment;

import gui.graphics.GraphicEntity;

import java.util.Iterator;

import game.Action;
import game.Hero;
public class Square extends GraphicEntity{

	public static final byte green = 0;
	public static final byte darkGreen = 1;
	public static final byte blue = 2;


	public Square(byte colour, int bufferSize, Iterator<Float> floats) {
		super("squares");
		this.setFrame(colour);
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
	public Square(byte colour, float width, float height) {
		super("squares");
		adjust(width,height);
		this.setFrame(colour);
	}
	public Square(byte colour, float size) {
		this(colour,size,size);
	}
	public boolean isFunctional() {
		return false;
	}

}
