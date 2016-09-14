package editor.field;

import java.util.ArrayList;
import java.util.List;

import game.menu.MenuButton;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;

public class FieldEditor <SubjectType extends Object> extends MenuButton{

	private List<Float> xOffsets = new ArrayList<Float>();
	private List<Float> yOffsets = new ArrayList<Float>();

	private TextFieldComponent<SubjectType,?>[] onTypes;
	private OnClickFieldComponent<SubjectType>[] onClicks;
	private int currentOnType = 0;

	public FieldEditor(String names,
			TextFieldComponent<SubjectType,?>[] onTypes,
			OnClickFieldComponent<SubjectType>[] onClicks) {
		super(names);
		this.onTypes = onTypes;
		this.onClicks = onClicks;
		for(int i=0;i<onTypes.length-1;++i){			
			onTypes[i].setNext(onTypes[i+1]);
		}
		String[] nameSplit = names.split("\n");
		text.setWidthFactor(1f);
		text.setHeightFactor(1f);
		resize(0.3f,0.13f);
		for(int i=0;i<nameSplit.length;++i){
			float dx = 0.065f;
			for(char c:nameSplit[i].toCharArray()){
				dx+=0.025f*Hub.renderer.letterWidths.get("impact").get(c)*14f/16f;
			}
			xOffsets.add(dx);
			yOffsets.add(0.025f*(nameSplit.length-1)+0.005f+0.025f*(-i));
		}
		for(int i=0;i<onClicks.length;++i){
			xOffsets.add(0.02f);
			yOffsets.add(getHeight()-0.04f-0.008f-getHeight()*((float)i)/onClicks.length);
		}
		for(TextFieldComponent<SubjectType,?> tfc:onTypes){
			addChild(tfc);			
			tfc.setParent(this);
		}
		for(OnClickFieldComponent<SubjectType> oc:onClicks){
			addChild(oc);
		}
		reposition(getX(),getY());
	}
	public void nextType() {
		++this.currentOnType;
	}
	@Override
	public void resize(float x, float y){
		super.resize(x, y);
		if(onClicks==null)return;
		for(int i=0;i<onClicks.length;++i){
			onClicks[i].resize(0.04f, 0.04f);
		}
		if(onTypes==null)return;
		for(int i=0;i<onTypes.length;++i){
			if(i>=xOffsets.size())return;
			onTypes[i].resize(getWidth()-xOffsets.get(i)-getChild(2).getWidth()/2f, 0.025f);
		}
	}
	@Override
	public float offsetX(int index){
		if(index<3){
			return super.offsetX(index);
		}
		else if(index==3){
			return 0.065f;
		}
		else {
			return xOffsets.get(index-4);
		}
	}
	@Override
	public float offsetY(int index){
		if(index<3){
			return super.offsetY(index);
		}
		else if(index==3){
			return 0.08f;
		}
		else {
			return yOffsets.get(index-4);
		}
	}
	@Override
	public boolean onClick(MotionEvent e){
		if(e.getAction()==MotionEvent.ACTION_UP){
			for(OnClickFieldComponent<SubjectType> ocfc:onClicks){
				if(ocfc.isWithin(e.getX(), e.getY())){
					ocfc.performOnRelease(e);
					return true;
				}
			}
			for(TextFieldComponent<SubjectType, ?> tfc:onTypes){
				if(tfc.isWithin(e.getX(), e.getY())){
					changeOnTypeTo(tfc);
					return true;
				}
			}
			if(!isWithin(e.getX(),e.getY())){
				onTypes[currentOnType].advance(onTypes[currentOnType].getText());
				Gui.removeOnType(onTypes[currentOnType]);
				Gui.removeOnClick(this);
				this.setVisible(false);
			}
		}
		return false;
	}
	@Override
	public KeyBoardListener getDefaultKeyBoardListener(){
		if(onTypes.length>0){
			return onTypes[0];
		}
		else return null;
	}
	public void updateWith(SubjectType subject){
		currentOnType=0;
		for(TextFieldComponent<SubjectType,?> tfc:onTypes){
			tfc.setTarget(subject);
			tfc.updateWith(subject);
		}
		for(OnClickFieldComponent<SubjectType> ocfc:onClicks){
			ocfc.setTarget(subject);
			ocfc.updateWith(subject);
		}
	}
	public void changeOnTypeTo(TextFieldComponent<SubjectType, ?> textField) {
		int i=0;
		for(;!onTypes[i].equals(textField);++i);
		if(currentOnType!=i){
			onTypes[currentOnType].advance(onTypes[currentOnType].getText());
		}
		Gui.removeOnType(onTypes[currentOnType]);
		Gui.giveOnType(onTypes[i]);
		currentOnType=i;
	}
}
