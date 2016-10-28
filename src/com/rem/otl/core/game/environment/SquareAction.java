package com.rem.otl.core.game.environment;

import com.rem.otl.core.game.Action;

public interface SquareAction <SubjectType,TargetType> extends Action<SubjectType>, Saveable {
	public int targetType();
	public void setTarget(TargetType target);
	public int getIndex();
}
