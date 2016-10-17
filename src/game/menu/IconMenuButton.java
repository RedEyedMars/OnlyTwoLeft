package game.menu;

import gui.graphics.GraphicEntity;
import gui.inputs.MotionEvent;
import main.Hub;

public class IconMenuButton extends MenuButton {
	protected GraphicEntity icon;
	public IconMenuButton(String textureName, int textureIndex){
		super("");
		icon = new GraphicEntity(textureName,Hub.MID_LAYER);
		icon.setFrame(textureIndex);
		addChild(icon);
	}
	
	@Override
	public float offsetX(int index){
		if(getChild(index)==icon){
			return getWidth()/2f-icon.getWidth()/2f;
		}
		else return super.offsetX(index);
	}
	@Override
	public float offsetY(int index){
		if(getChild(index)==icon){
			return getHeight()/2f-icon.getHeight()/2f;
		}
		else return super.offsetY(index);
	}
	@Override
	public void resize(float w, float h){
		super.resize(w, h);
		if(icon!=null){
			mid.resize(w*0.2f,h);
			left.resize(w*0.4f,h);
			right.resize(w*0.4f,h);			
			icon.resize(0.1f,0.1f);
		}
	}
}
