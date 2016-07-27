package game.environment;

import java.util.List;

import game.Action;

public interface SquareAction <Type> extends Action<Type> {
	public int numberOfTargets();
	public void setTarget(Square square);
	public void saveTo(List<Object> saveTo);
	public int getIndex();
}
