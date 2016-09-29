package editor.program;

import editor.Button;
import editor.ButtonAction;
import editor.field.FieldEditor;
import editor.field.FloatFieldComponent;
import editor.field.OnClickFieldComponent;
import editor.field.TextFieldComponent;
import game.environment.program.ProgramAction;
import game.environment.program.condition.ProgramCondition;
import game.environment.update.UpdateAction;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;

public abstract class ActionEditor <TargetType extends Settable> extends Button implements DataRetriever, Cloneable{
	protected ProgramAction action;
	private StateSquare state = null;

	private GraphicEntity arrow;
	protected ProgramSquareEditor editor;
	private ArrowButton parentArrow = null;

	private FieldEditor<TargetType> valueEditor;
	protected TargetType target;
	public ActionEditor(ProgramSquareEditor editor,
			String textureName, Integer frame, 
			final ProgramAction action, TargetType initialTarget) {
		super(textureName, frame,
				" - "+ProgramAction.actionNames.get(action.getIndex())+" action", null,null);
		this.editor = editor;
		final ActionEditor self = this;
		this.action = action;
		this.arrow = new GraphicEntity("editor_arrows",1);
		this.arrow.setFrame(0);
		addChild(arrow);
		this.target = initialTarget;
		valueEditor =  new FieldEditor<TargetType>("",
				new TextFieldComponent[]{},
				new OnClickFieldComponent[]{});
		valueEditor.setVisible(false);
		addChild(valueEditor);
		setOnClick(new ButtonAction(){

			@Override
			public void act(MotionEvent event) {
				self.retrieveData();
			}
		});

		if(initialTarget!=null){
			setOnRelease(new ButtonAction(){
				@Override
				public void act(MotionEvent event){
					//Summon UpdateStyle updateField
					valueEditor.clearOnClicks();
					valueEditor.clearOnTypes();
					valueEditor.changeText("");

					StringBuilder text = new StringBuilder();
					for(int i=0;i<target.copiableValueIds().length;++i){
						final int valueId = target.copiableValueIds()[i];
						text.append(target.copiableValueNames()[i]);
						text.append(":\n");
						valueEditor.addOnType(new FloatFieldComponent<TargetType>("impact"){
							@Override
							public void act(Float subject) {
								target.setValue(valueId,subject);
							}

							@Override
							public TargetType updateWith(TargetType subject) {
								changeTextOnLine(""+subject.getValue(valueId), 0);
								return subject;
							}});
					}
					for(int i=0;i<target.copiableIntIds().length;++i){
						final int intId = target.copiableIntIds()[i];
						valueEditor.addOnClick(new OnClickFieldComponent<TargetType>(
								target.copiableIntTextureNames()[i],
								target.copiableIntTextureRanges()[i*2],target.copiableIntTextureRanges()[i*2+1]){
							@Override
							public void act(Integer subject) {
								target.setValue(intId,(int)subject);
							}

							@Override
							public void updateWith(TargetType subject) {
								setFrame(subject.getInt(intId));
							}});
					}

					valueEditor.changeText(text.toString());

					valueEditor.updateWith(target);
					valueEditor.setVisible(true);
					valueEditor.resize(0.3f, 0f);
					valueEditor.reposition(self.getX(), self.getY()+self.getHeight());
					Gui.giveOnClick(valueEditor);
					Gui.giveOnType(valueEditor.getDefaultKeyBoardListener());

				}
			});
		}
	}

	@Override
	public boolean onClick(MotionEvent e){
		if(MotionEvent.ACTION_UP==e.getAction()){
			if(state==null){
				reposition(e.getX()-getWidth()/2f,
						e.getY()-getHeight()/2f);
				editor.removeTransitioningActionEditor(this);
				this.state = editor.getStateRoot().addActionEditor(this);
				Gui.removeOnClick(this);
			}
			if(parentArrow!=null){
				parentArrow.free();
			}
		}
		return true;
	}

	public boolean onHover(MotionEvent e){
		reposition(e.getX()-getWidth()/2f,
				e.getY()-getHeight()/2f);
		return true;
	}
	@Override
	public float offsetX(int index){
		if(index>1){
			return -getChild(index).getWidth()-0.005f;
		}
		else {
			return super.offsetX(index);
		}
	}
	@Override
	public void resize(float dx, float dy){
		super.resize(dx,dy);
		arrow.resize(dx*2f,dy);
	}

	public void setParentArrowButton(ArrowButton arrow) {
		this.parentArrow = arrow;
	}


}
