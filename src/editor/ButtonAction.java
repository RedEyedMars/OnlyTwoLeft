package editor;

import java.util.List;

import game.Action;

public abstract class ButtonAction implements Action<Editor>{
	@Override
	public void saveTo(List<Object> saveTo) {		
	}
	@Override
	public int getIndex(){
		return 0;
	}

}
