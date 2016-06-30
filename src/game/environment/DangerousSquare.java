package game.environment;

import java.util.Iterator;

import game.Hero;

public class DangerousSquare extends FunctionalSquare{
	public static SquareAction hazard = new SquareAction(){
		@Override
		public void act(Hero subject) {
			subject.endGame();
		}};
	public DangerousSquare(byte colour, float size) {
		super(colour, size, hazard);
	}
	public DangerousSquare(byte colour, float width, float height) {
		super(colour, width, height, hazard);
	}
	public DangerousSquare(byte colour, int bufferSize, Iterator<Float> floats) {
		super(colour,bufferSize,floats,hazard);
	}

}
