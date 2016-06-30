package game.environment;

import java.util.Iterator;

import game.Action;
import game.Hero;

public class WallSquare extends FunctionalSquare{

	
	public static SquareAction impassible = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.backup(self);
		}};
	public WallSquare(byte colour, float size) {
		super(colour, size, impassible);
	}
	public WallSquare(byte colour, float width, float height) {
		super(colour, width, height, impassible);
	}
	public WallSquare(byte colour, int bufferSize, Iterator<Float> floats) {
		super(colour,bufferSize,floats,impassible);
	}

}
