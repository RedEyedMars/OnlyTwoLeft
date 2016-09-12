package editor;

import game.Action;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;

/**
 * This class wraps the a {@link gui.graphics.GraphicEntity} object with an {@link game.Action} which is used by an {@link editor.Editor}.
 * An example use case would be the buttons that determine colour of the created squares. 
 * @author Geoffrey
 *
 */
public class Button extends GraphicEntity{

	//The action to perform when this Button is clicked.
	private Action<Object> onClick;
	private GraphicEntity icon;

	protected Integer frame;

	public Button(String textureName, Action<Object> onClick) {
		this(textureName,null,onClick);
	}
	public Button(Action<Object> onClick) {
		this("blank",0,onClick);
	}
	public Button(String textureName, Integer frame, Action<Object> onClick) {
		super("editor_button",1);
		this.onClick = onClick;

		if(frame>=0){
			icon = new GraphicEntity(textureName,1);
			icon.setFrame(frame);
			this.frame = frame;
			addChild(icon);
		}
		else {
			icon = new GraphicEntity("editor_button",1);
			icon.setFrame(1);
			this.frame = frame;
			addChild(icon);
		}
	}

	@Override
	public void performOnClick(MotionEvent e){		
		onClick.act(null);
	}
	public void setAction(Action<Object> action) {
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
	public GraphicEntity getIcon() {
		return icon;
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

	/**
	 * Buttons cannot be visually turned on by their parent {@link game.environment.Square}.
	 */
	@Override
	public void turnOn(){		
	}
	/**
	 * Buttons cannot be visually turned off by their parent {@link game.environment.Square}.
	 */
	@Override
	public void turnOff(){		
	}
}
