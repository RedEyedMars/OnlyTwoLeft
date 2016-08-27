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
	private Action<Editor> onClick;
	//The parent editor, this is the editor that will be fed to the onClick action.
	private Editor parent;
	//
	protected Integer frame;

	public Button(String textureName, Editor parent, Action<Editor> onClick) {
		this(textureName,null,parent,onClick);
	}
	public Button(Editor parent, Action<Editor> onClick) {
		this("blank",0,parent,onClick);
	}
	public Button(String textureName, Integer frame, Editor parent, Action<Editor> onClick) {
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
	public void setAction(Action<Editor> action) {
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
