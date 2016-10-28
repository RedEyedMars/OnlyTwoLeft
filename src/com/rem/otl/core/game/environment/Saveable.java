package com.rem.otl.core.game.environment;

import java.util.List;

public interface Saveable {
	public void saveTo(List<Object> saveTo);
	public int saveType();
}
