package editor.field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.environment.program.condition.ProgramCondition;
import game.menu.MenuButton;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import main.Hub;

public class FieldEditor <SubjectType extends Object> extends MenuButton{

	private List<Float> xOffsets = new ArrayList<Float>();
	private List<Float> yOffsets = new ArrayList<Float>();

	private List<TextFieldComponent<SubjectType,?>> onTypes;
	private List<OnClickFieldComponent<SubjectType>> onClicks;
	private int currentOnType = 0;

	public FieldEditor(String names,
			TextFieldComponent<SubjectType,?>[] onTypes,
			OnClickFieldComponent<SubjectType>[] onClicks) {
		super(names);
		this.onTypes = new ArrayList<TextFieldComponent<SubjectType,?>>();
		this.onClicks = new ArrayList<OnClickFieldComponent<SubjectType>>();
		this.onTypes.addAll(Arrays.asList(onTypes));
		this.onClicks.addAll(Arrays.asList(onClicks));
		for(int i=0;i<onTypes.length-1;++i){			
			onTypes[i].setNext(onTypes[i+1]);
		}
		text.setWidthFactor(1f);
		text.setHeightFactor(1f);
		for(TextFieldComponent<SubjectType,?> tfc:onTypes){
			addChild(tfc);			
			tfc.setParent(this);
		}
		for(OnClickFieldComponent<SubjectType> oc:onClicks){
			addChild(oc);
		}
		resize(getWidth(),getHeight());
	}
	public void nextType() {
		++this.currentOnType;
		if(currentOnType>=onTypes.size()){
			currentOnType=onTypes.size()-1;
		}
	}
	@Override
	public void resize(float x, float y){
		if(onClicks==null||onTypes==null){
			super.resize(x,y);
			return;
		}
		String[] nameSplit = this.getText().split("\n");
		xOffsets.clear();
		yOffsets.clear();
		int nameIndex=0;
		int clickIndex=0;
		for(int i=4;i<size();++i){
			if(getChild(i) instanceof TextFieldComponent){
				float dx = 0.065f;
				for(char c:nameSplit[nameIndex].toCharArray()){
					dx+=0.025f*Hub.renderer.letterWidths.get("impact").get(c)*14f/16f;
				}
				xOffsets.add(dx);
				yOffsets.add(0.025f*(nameSplit.length-1)+0.005f+0.025f*(-nameIndex));
				++nameIndex;
			}
			else if(getChild(i) instanceof OnClickFieldComponent){
				xOffsets.add(0.02f);
				yOffsets.add(getHeight()-0.04f-0.008f-getHeight()*((float)clickIndex)/onClicks.size());
				++clickIndex;
			}
		}
		super.resize(0.3f,0.025f*(nameSplit.length)+0.03f);
		for(int i=0;i<onClicks.size();++i){
			onClicks.get(i).resize(0.04f, 0.04f);
		}
		for(int i=0;i<onTypes.size();++i){
			if(i>=xOffsets.size())return;
			onTypes.get(i).resize(getWidth()-xOffsets.get(i)-getChild(2).getWidth()/2f, 0.025f);
		}
		reposition(getX(),getY());
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
				onTypes.get(currentOnType).advance(onTypes.get(currentOnType).getText());
				Gui.removeOnType(onTypes.get(currentOnType));
				Gui.removeOnClick(this);
				this.setVisible(false);
			}
		}
		return false;
	}
	@Override
	public KeyBoardListener getDefaultKeyBoardListener(){
		if(onTypes.size()>0){
			return onTypes.get(0);
		}
		else return null;
	}
	public void updateWith(SubjectType subject){
		currentOnType=0;
		onTypes.get(0).updateChain(subject);
		for(OnClickFieldComponent<SubjectType> ocfc:onClicks){
			ocfc.setTarget(subject);
			ocfc.updateWith(subject);
		}
	}
	public void changeOnTypeTo(TextFieldComponent<SubjectType, ?> textField) {
		int i=0;
		for(;!onTypes.get(i).equals(textField)&&i<onTypes.size();++i);
		if(currentOnType!=i){
			onTypes.get(currentOnType).advance(onTypes.get(currentOnType).getText());
		}
		Gui.removeOnType(onTypes.get(currentOnType));
		Gui.giveOnType(onTypes.get(i));
		currentOnType=i;
		if(currentOnType>=onTypes.size()){
			currentOnType=onTypes.size()-1;
		}
	}
	public List<TextFieldComponent<SubjectType, ?>> getOnTypes() {
		return onTypes;
	}
	public void addOnType(TextFieldComponent<SubjectType, ?> onType) {
		onTypes.add(onType);
		addChild(onType);
	}
	public void clearOnTypes() {
		while(!onTypes.isEmpty()){
			removeChild(onTypes.remove(0));
		}
	}

	public void addOnClick(OnClickFieldComponent<SubjectType> onClick) {
		onClicks.add(onClick);
		addChild(onClick);
	}
	public void clearOnClicks() {
		while(!onClicks.isEmpty()){
			removeChild(onClicks.remove(0));
		}
	}
}
