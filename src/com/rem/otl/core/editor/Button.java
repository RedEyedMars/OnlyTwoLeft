package com.rem.otl.core.editor;

import com.rem.otl.core.game.Action;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

/**
 * This class wraps the a {@link com.rem.otl.core.gui.graphics.GraphicEntity} object with an {@link com.rem.otl.core.game.Action} which is used by an {@link com.rem.otl.core.editor.Editor}.
 * An example use case would be the buttons that determine colour of the created squares. 
 * @author Geoffrey
 *
 */
public class Button extends GraphicEntity{

	//The action to perform when this Button is clicked.
	private Action<ClickEvent> onClick;
	private Action<ClickEvent> onRelease;
	private GraphicEntity icon;
	private GraphicEntity null_icon;

	protected Integer frame;

	private String description;

	public Button(String textureName,String description, Action<ClickEvent> onClick, Action<ClickEvent> onRelease) {
		this(textureName,-2,description,onClick, onRelease);
	}
	public Button(String description, Action<ClickEvent> onClick, Action<ClickEvent> onRelease) {
		this("blank",-2,description,onClick, onRelease);
	}
	public Button(String textureName, Integer frame, String description, Action<ClickEvent> onClick, Action<ClickEvent> onRelease) {
		super("editor_button",Hub.MID_LAYER);
		this.onClick = onClick;
		this.onRelease = onRelease;
		this.description = description;

		icon = new GraphicEntity(textureName,Hub.MID_LAYER);
		addChild(icon);
		
		null_icon = new GraphicEntity("editor_button",Hub.MID_LAYER);
		null_icon.setFrame(1);
		addChild(null_icon);
		setFrame(frame);
	}

	@Override
	public void performOnClick(ClickEvent e){
		if(onClick!=null){
			onClick.act(e);
		}
	}
	@Override
	public void performOnRelease(ClickEvent e){
		if(onRelease!=null){
			onRelease.act(e);
		}
	}
	public void setOnClick(Action<ClickEvent> action) {
		this.onClick = action;
	}
	public void setOnRelease(Action<ClickEvent> action) {
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
	 * Buttons cannot be visually turned on by their parent {@link com.rem.otl.core.game.environment.Square}.
	 */
	@Override
	public void turnOn(){		
	}
	/**
	 * Buttons cannot be visually turned off by their parent {@link com.rem.otl.core.game.environment.Square}.
	 */
	@Override
	public void turnOff(){		
	}
	

	public String getDescription() {
		return description;
	}
}
