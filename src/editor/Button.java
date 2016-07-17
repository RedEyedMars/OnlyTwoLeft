package editor;

import game.Action;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;
import main.Hub;

public class Button <T extends Object> extends GraphicEntity{

	private Action<T> onClick;
	private T parent;
	protected Integer frame;
	private GraphicEntity selected;
	public Button(String textureName, T parent, Action<T> onClick) {
		this(textureName,null,parent,onClick);
	}
	public Button(T parent, Action<T> onClick) {
		this("blank",0,parent,onClick);
	}
	public Button(String textureName, Integer frame, T parent, Action<T> onClick) {
		super("editor_button",1);
		this.onClick = onClick;
		this.parent = parent;

		if(frame>=0){
			GraphicEntity e = new GraphicEntity(textureName,1);
			e.setFrame(frame);
			this.frame = frame;
			addChild(e);
		}
		else {
			GraphicEntity e = new GraphicEntity("editor_button",1);
			e.setFrame(1);
			this.frame = frame;
			addChild(e);
		}
	}

	@Override
	public void performOnClick(MotionEvent e){		
		onClick.act(parent);
	}
	public void setAction(Action<T> action) {
		this.onClick = action;
	}
	public void setSelected(boolean b) {
		if(b){
			setFrame(2);
		}
		else setFrame(0);
	}

	public boolean isSelected() {
		return textureIndex()==2;
	}

	@Override
	public void adjust(float width, float height, float dWidth, float dHeight){
		if(frame>=0){
			super.adjust(width, height, dWidth, dHeight);
		}
		else{
			super.adjust(width, height);
		}
	}

	@Override
	public void turnOn(){		
	}
	@Override
	public void turnOff(){		
	}
}
