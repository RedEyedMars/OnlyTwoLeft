package com.rem.otl.core.editor.field;

import com.rem.otl.core.game.Action;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public abstract class OnClickFieldComponent  <TargetType extends Object> extends GraphicEntity implements Action<Integer> {

	protected int first;
	protected int last;
	protected FieldEditor<TargetType> parentField;
	protected TargetType target;
	private GraphicEntity nullShower;
	private int frame = -1;

	public OnClickFieldComponent(String textureName, int firstInRange, int lastInRange) {
		super(textureName,Hub.MID_LAYER);
		this.first = firstInRange;
		this.last = lastInRange;
		
		this.nullShower = new GraphicEntity("editor_button",Hub.MID_LAYER);
		nullShower.setFrame(1);
		addChild(this.nullShower);
	}
	public void setTarget(TargetType target) {
		this.target = target;
	}
	public void setParent(FieldEditor<TargetType> parent) {
		this.parentField =  parent;
	}
	@Override
	public void performOnRelease(ClickEvent e){		
		if(parentField!=null&&!parentField.isVisible())return;
		++frame;
		if(frame>=last){
			this.setFrame(first);
		}
		else {
			this.setFrame(frame);
		}
		act(frame);
	}
	
	@Override
	public void setFrame(int frame){
		if(frame==-1){
			turnOff();
			nullShower.turnOn();
		}
		else {
			turnOn();
			nullShower.turnOff();
			super.setFrame(frame);
		}
		this.frame = frame;
	}

	public abstract void updateWith(TargetType subject);


}
