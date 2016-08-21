package game.environment;

import java.util.List;

import game.Action;

public interface SquareAction <SubjectType,TargetType> extends Action<SubjectType> {
	public int targetType();
	public void setTarget(TargetType target);
	public void saveTo(List<Object> saveTo);
	public int getIndex();
}
