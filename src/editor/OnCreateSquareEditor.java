package editor;

import java.util.ArrayList;
import java.util.List;

import game.environment.OnCreateAction;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;

public class OnCreateSquareEditor extends GraphicView implements KeyBoardListener{

	private Editor editor;
	private List<OnCreateAction> actions = new ArrayList<OnCreateAction>();

	public OnCreateSquareEditor(Editor parent){
		super();
		this.editor = parent;
	}
	
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(45==keycode){
			saveAndReturnToEditor();
		}
	}

	private void saveAndReturnToEditor() {
		for(OnCreateAction action:actions){
			
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return true;
	}

}
