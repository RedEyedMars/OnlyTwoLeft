package com.rem.otl.core.game.menu;

import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class MenuButton extends GraphicEntity {
	private MenuButton self = this;
	protected GraphicText text;
	protected GraphicEntity left;
	protected GraphicEntity mid;
	protected GraphicEntity right;
	public MenuButton(String name) {
		super("blank",Hub.BOT_LAYER);
		left = new GraphicEntity("speech_bubble",Hub.MID_LAYER){
			@Override
			public void performOnClick(ClickEvent e){
				self.performOnClick(e);
			}
		};
		left.resize(0.1f, 0.15f);
		left.reposition(0.2f,0f);
		left.setFrame(0);
		addChild(left);
		mid = new GraphicEntity("speech_bubble",Hub.MID_LAYER){
			@Override
			public void performOnClick(ClickEvent e){
				self.performOnClick(e);
			}
		};
		mid.resize(0.4f, 0.15f);
		mid.reposition(0.3f,0f);
		mid.setFrame(1);
		addChild(mid);
		right = new GraphicEntity("speech_bubble",Hub.MID_LAYER){
			@Override
			public void performOnClick(ClickEvent e){
				self.performOnClick(e);
			}
		};
		right.resize(0.1f, 0.15f);
		right.reposition(0.7f,0f);
		right.setFrame(2);
		addChild(right);
		text = new GraphicText("impact",name,Hub.MID_LAYER){
			@Override
			public void reposition(float x, float y){
				super.reposition(x, y);
			}
		};
		text.setJustified(GraphicText.MIDDLE_JUSTIFIED);
		text.setFontSize(GraphicText.FONT_SIZE_LARGE);
		addChild(text);
		resize(0.6f,0.15f);
	}
	public MenuButton(String name,boolean inverted) {
		super("blank",Hub.BOT_LAYER);
		left = new GraphicEntity("speech_bubble_inverted",Hub.MID_LAYER){
			@Override
			public void performOnClick(ClickEvent e){
				self.performOnClick(e);
			}
		};
		left.resize(0.1f, 0.15f);
		left.reposition(0.2f,0f);
		left.setFrame(0);
		addChild(left);
		mid = new GraphicEntity("speech_bubble_inverted",Hub.MID_LAYER){
			@Override
			public void performOnClick(ClickEvent e){
				self.performOnClick(e);
			}
		};
		mid.resize(0.4f, 0.15f);
		mid.reposition(0.3f,0f);
		mid.setFrame(1);
		addChild(mid);
		right = new GraphicEntity("speech_bubble_inverted",Hub.MID_LAYER){
			@Override
			public void performOnClick(ClickEvent e){
				self.performOnClick(e);
			}
		};
		right.resize(0.1f, 0.15f);
		right.reposition(0.7f,0f);
		right.setFrame(2);
		addChild(right);
		text = new GraphicText("impactWhite",name,Hub.MID_LAYER);
		text.setFontSize(GraphicText.FONT_SIZE_LARGE);
		text.setJustified(GraphicText.MIDDLE_JUSTIFIED);
		addChild(text);
		resize(0.6f,0.15f);
	}
	public float offsetY(int index){
		return index==3?0.02f:0f;
	}
	public float offsetX(int index){
		if(getChild(index) instanceof GraphicText){
			GraphicText t = (GraphicText)getChild(index);
			if(t.isJustified(GraphicText.MIDDLE_JUSTIFIED)){
				return 0.0f;
			}
			else if(t.isJustified(GraphicText.LEFT_JUSTIFIED)){
				return 0.05f;
			}
			else if(t.isJustified(GraphicText.RIGHT_JUSTIFIED)){
				return -0.05f;
			}
		}
		return index==2?getChild(0).getWidth()+getChild(1).getWidth():
			   index==1?getChild(0).getWidth():0f;
	}
	@Override
	public void resize(float x, float y){
		super.resize(x, y);
		left.resize(x*0.1f/0.6f, y);
		mid.resize(x*0.4f/0.6f, y);
		right.resize(x*0.1f/0.6f, y);
		text.resize(x, y);
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

