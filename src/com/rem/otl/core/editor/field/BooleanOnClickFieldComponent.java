package com.rem.otl.core.editor.field;


public abstract class BooleanOnClickFieldComponent <TargetType extends Object> extends OnClickFieldComponent<TargetType> {

	private boolean zeroIsTrue ;
	public BooleanOnClickFieldComponent(String textureName, boolean zeroIsTrue, int offset) {
		super(textureName,0+offset,2+offset);
		this.zeroIsTrue = zeroIsTrue;
	}
	@Override
	public void act(Integer integer){
		if(zeroIsTrue){
			act(integer==first);
		}
		else {
			act(integer==last-1);
		}
	}
	
	public abstract void act(Boolean subject);	

}
