package editor.field;

import game.Action;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;

public abstract class OnClickFieldComponent  <TargetType extends Object> extends GraphicEntity implements Action<Integer> {

	protected int first;
	protected int last;
	protected FieldEditor<TargetType> parentField;
	protected TargetType target;
	private GraphicEntity nullShower;
	private int frame = -1;

	public OnClickFieldComponent(String textureName, int firstInRange, int lastInRange) {
		super(textureName,1);
		this.first = firstInRange;
		this.last = lastInRange;
		this.listenToRelease = true;
		
		this.nullShower = new GraphicEntity("editor_button",1);
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
	public void performOnRelease(MotionEvent e){		
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
