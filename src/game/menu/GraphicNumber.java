package game.menu;

import gui.graphics.GraphicEntity;

public class GraphicNumber extends GraphicEntity{

	private int value = 0;
	
	public GraphicNumber() {
		super("numbers");
		this.setVisible(false);
		for(int i=0;i<9;++i){
			this.addChild(new Digit(i));
			this.getChild(i).adjust(0.05f, 0.05f);
		}
		this.setX(0f);
	}
	
	@Override
	public float offsetX(int i){
		return (8-i)*0.031f;
	}
	
	@Override
	public void setX(float x){
		if(x>0.7f){
			super.setX(0.7f);
		}
		else super.setX(x);
	}
	
	private class Digit extends GraphicEntity {

		private int upper;
		private int lower;

		public Digit(int position) {
			super("numbers");
			this.upper = (int) Math.pow(10,position+1);
			this.lower = (int) Math.pow(10,position);
		}
		
		public void update(){
			if(value<lower&&lower>1){
				this.setVisible(false);
			}
			else {
				this.setVisible(true);
				this.setFrame((value%upper)/lower);
			}
		}
		
	}

	public void setValue(int i) {
		this.value = i;
		
	}

	public int getValue() {
		return value;
	}

	public void inc() {
		++value;
	}

}
