package com.rem.otl.core.game.environment.program.condition;

public interface Condition <SubjectType extends Object>{
	public boolean satisfies(SubjectType subject);
}
