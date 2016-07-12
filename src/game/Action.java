package game;

import java.util.List;

public interface Action <T>{

	public void act(T subject);
	public void saveTo(List<Object> saveTo);
	public int getIndex();
}
