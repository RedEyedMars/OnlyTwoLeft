package editor;

import java.util.ArrayList;
import java.util.List;

import game.environment.OnCreateAction;
import game.environment.OnCreateSquare;
import gui.Gui;
import gui.graphics.GraphicView;
import gui.inputs.KeyBoardListener;

public class OnCreateSquareEditor extends GraphicView{

	private MapEditor editor;
	private List<OnCreateAction> actions = new ArrayList<OnCreateAction>();
	private float square_x;
	private float square_y;
	private float square_w;
	private float square_h;
	
	private TextWriter writer;

	public OnCreateSquareEditor(MapEditor parent,float x, float y, float w, float h){
		super();
		this.editor = parent;
		this.square_x = x;
		this.square_y = y;
		this.square_w = w;
		this.square_h = h;
		
		writer = new TextWriter("");
		Gui.giveOnType(writer);
		addChild(writer);
	}

	public void keyCommand(boolean b, char c, int keycode) {
		if(45==keycode){
			saveAndReturnToEditor();
		}
	}

	private void saveAndReturnToEditor() {
		final List<Integer> ints = new ArrayList<Integer>();
		final List<Float> floats = new ArrayList<Float>();
		List<Object> probe = new ArrayList<Object>(){
			@Override
			public boolean add(Object obj){
				if(obj instanceof Integer){
					return ints.add((Integer) obj);
				}
				else if(obj instanceof Float){
					return floats.add((Float) obj);
				}
				return false;
			}
		};
		floats.add(square_x);
		floats.add(square_y);
		floats.add(square_w);
		floats.add(square_h);
		ints.add(actions.size());
		for(OnCreateAction action:actions){
			action.saveTo(probe);
		}
		OnCreateSquare square = new OnCreateSquare(0,3,4,ints.iterator(),floats.iterator());
		editor.addSquare(square);
		Gui.setView(editor);
	}

}
