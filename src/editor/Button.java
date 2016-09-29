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
	private Action<MotionEvent> onClick;
	private Action<MotionEvent> onRelease;
	private GraphicEntity icon;
	private GraphicEntity null_icon;

	protected Integer frame;

	private String description;

	public Button(String textureName,String description, Action<MotionEvent> onClick, Action<MotionEvent> onRelease) {
		this(textureName,-2,description,onClick, onRelease);
	}
	public Button(String description, Action<MotionEvent> onClick, Action<MotionEvent> onRelease) {
		this("blank",-2,description,onClick, onRelease);
	}
	public Button(String textureName, Integer frame, String description, Action<MotionEvent> onClick, Action<MotionEvent> onRelease) {
		super("editor_button",1);
		this.onClick = onClick;
		this.onRelease = onRelease;
		this.description = description;

		icon = new GraphicEntity(textureName,1);
		addChild(icon);
		
		null_icon = new GraphicEntity("editor_button",1);
		null_icon.setFrame(1);
		addChild(null_icon);
		setFrame(frame);
	}

	@Override
	public void performOnClick(MotionEvent e){
		if(onClick!=null){
			onClick.act(e);
		}
	}
	@Override
	public void performOnRelease(MotionEvent e){
		if(onRelease!=null){
			onRelease.act(e);
		}
	}
	public void setOnClick(Action<MotionEvent> action) {
		this.onClick = action;
	}
	public void setOnRelease(Action<MotionEvent> action) {
		this.onRelease = action;
	}
	public void setSelected(boolean b) {
		if(b){
			super.setFrame(2);
		}
		else super.setFrame(0);
	}

	public boolean isSelected() {
		return getFrame()==2;
	}
	public GraphicEntity getIcon() {
		return icon;
	}
	@Override
	public void setFrame(int frame){

		this.frame = frame;
		if(frame==-2){
			null_icon.turnOff();
			icon.turnOff();
		}		
		else if(frame==-1){
			null_icon.setVisible(true);
			icon.setVisible(false);
		}
		else {
			null_icon.setVisible(false);
			icon.setVisible(true);
			icon.setFrame(frame);
		}
	}
	@Override
	public void setVisible(boolean vis){
		super.setVisible(vis);
		if(vis){
			setFrame(frame);
		}
	}
	@Override
	public void resize(float width, float height, float dWidth, float dHeight){
		if(frame>=0){
			super.resize(width, height, dWidth, dHeight);
		}
		else{
			super.resize(width, height);
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
	

	public String getDescription() {
		return description;
	}
}
