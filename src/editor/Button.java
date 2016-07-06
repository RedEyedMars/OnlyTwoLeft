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
	}
	
	@Override
	public void performOnClick(MotionEvent e){		
		onClick.act(parent);
	}

}
