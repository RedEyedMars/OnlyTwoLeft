package editor;

import game.Action;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;
import main.Hub;

public class Button <T extends Object> extends GraphicEntity{

	private Action<T> onClick;
	private T parent;
	private String subTex;
	private Integer subFrame;
	private GraphicEntity selected;
	public Button(String textureName, T parent, Action<T> onClick) {
		this(textureName,null,parent,onClick);
	}
	public Button(String textureName,Integer frame, T parent, Action<T> onClick) {
		this(textureName,textureName,frame,parent,onClick);
	}
	public Button(String textureName,String subTexName, Integer frame, T parent, Action<T> onClick) {
		super(frame!=null?textureName:subTexName,1);
		this.onClick = onClick;
		this.parent = parent;
		this.subTex = subTexName;
		this.subFrame = frame;

		if(subFrame!=null){
			GraphicEntity e = new GraphicEntity(subTexName,1);
			e.setFrame(subFrame);
			addChild(e);
		}
		selected = new GraphicEntity("editor_select",1);
		selected.setVisible(false);
		addChild(selected);
	}
	
	@Override
	public void performOnClick(MotionEvent e){		
		onClick.act(parent);
	}
	public void setAction(Action<T> action) {
		this.onClick = action;
	}
	public void setSelected(boolean b) {
		selected.setVisible(b);
	}
	
	@Override
	public void adjust(float width, float height, float dWidth, float dHeight){
		super.adjust(width, height, dWidth, dHeight);
		getChild(0).adjust(dWidth, dHeight);
		getChild(1).adjust(width, height);
	}

	@Override
	public void turnOn(){		
	}
	@Override
	public void turnOff(){		
	}
}
