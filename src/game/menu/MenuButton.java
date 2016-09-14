package game.menu;

import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.MotionEvent;

public class MenuButton extends GraphicEntity {
	private MenuButton self = this;
	protected GraphicText text;
	protected GraphicEntity left;
	protected GraphicEntity mid;
	protected GraphicEntity right;
	public MenuButton(String name) {
		super("blank",0);
		left = new GraphicEntity("speech_bubble",1){
			@Override
			public void performOnClick(MotionEvent e){
				self.performOnClick(e);
			}
		};
		left.resize(0.1f, 0.15f);
		left.reposition(0.2f,0f);
		left.setFrame(0);
		addChild(left);
		mid = new GraphicEntity("speech_bubble",1){
			@Override
			public void performOnClick(MotionEvent e){
				self.performOnClick(e);
			}
		};
		mid.resize(0.4f, 0.15f);
		mid.reposition(0.3f,0f);
		mid.setFrame(1);
		addChild(mid);
		right = new GraphicEntity("speech_bubble",1){
			@Override
			public void performOnClick(MotionEvent e){
				self.performOnClick(e);
			}
		};
		right.resize(0.1f, 0.15f);
		right.reposition(0.7f,0f);
		right.setFrame(2);
		addChild(right);
		resize(0.6f,0.15f);
		text = new GraphicText("impact",name,1);
		text.setWidthFactor(1.4f);
		text.setHeightFactor(3f);
		text.resize(text.getWidth(), text.getHeight());
		text.reposition(0f,0f);
		addChild(text);
	}
	public MenuButton(String name,boolean inverted) {
		super("blank",0);
		left = new GraphicEntity("speech_bubble_inverted",1){
			@Override
			public void performOnClick(MotionEvent e){
				self.performOnClick(e);
			}
		};
		left.resize(0.1f, 0.15f);
		left.reposition(0.2f,0f);
		left.setFrame(0);
		addChild(left);
		mid = new GraphicEntity("speech_bubble_inverted",1){
			@Override
			public void performOnClick(MotionEvent e){
				self.performOnClick(e);
			}
		};
		mid.resize(0.4f, 0.15f);
		mid.reposition(0.3f,0f);
		mid.setFrame(1);
		addChild(mid);
		right = new GraphicEntity("speech_bubble_inverted",1){
			@Override
			public void performOnClick(MotionEvent e){
				self.performOnClick(e);
			}
		};
		right.resize(0.1f, 0.15f);
		right.reposition(0.7f,0f);
		right.setFrame(2);
		addChild(right);
		resize(0.6f,0.15f);
		text = new GraphicText("impactWhite",name,1);
		text.setWidthFactor(1.4f);
		text.setHeightFactor(3f);
		text.resize(text.getWidth(), text.getHeight());
		text.reposition(0f,0f);
		addChild(text);
	}
	public float offsetY(int index){
		return index==3?0.02f:0f;
	}
	public float offsetX(int index){
		return index==3?(getWidth()/2f)-(text.getText().length()*0.03f)/2f:
			   index==2?getChild(0).getWidth()+getChild(1).getWidth():
			   index==1?getChild(0).getWidth():0f;
	}
	@Override
	public void resize(float x, float y){
		super.resize(x, y);
		left.resize(x*0.1f/0.6f, y);
		mid.resize(x*0.4f/0.6f, y);
		right.resize(x*0.1f/0.6f, y);
	}
	public String getText() {
		return text.getText();
	}
	public void changeText(String name) {
		text.change(name);
		resize(getWidth(),getHeight());
		reposition(getX(),getY());
	}
}

