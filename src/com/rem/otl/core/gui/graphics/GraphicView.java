package com.rem.otl.core.gui.graphics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.inputs.EventHandler;
import com.rem.otl.core.gui.inputs.HoverEvent;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.gui.inputs.MouseListener;
import com.rem.otl.core.main.Hub;

public class GraphicView implements MouseListener{
	protected List<GraphicEntity> children = new ArrayList<GraphicEntity>();
	protected GraphicView root = null;

	public GraphicView(){
	}	

	public float offsetX(int i){
		return 0;
	}
	public float offsetY(int i){
		return 0;
	}
	public void reposition(float x, float y) {
		for(int i=0;i<children.size();++i){
			children.get(i).reposition(x+offsetX(i),y+offsetY(i));
		}
	}
	public void resize(float width, float height) {
		for(int i=0;i<children.size();++i){
			children.get(i).resize(width,height);
		}
	}

	public void resize(float width, float height, float dWidth, float dHeight){
		for(int i=0;i<children.size();++i){
			children.get(i).resize(dWidth,dHeight);
		}
	}
	public boolean isVisible(){
		return false;
	}


	public void onAddToDrawable() {
		for(int i=0;i<children.size();++i){
			children.get(i).onAddToDrawable();
		}
	}

	public void onRemoveFromDrawable() {
		for(int i=0;i<children.size();++i){
			children.get(i).onRemoveFromDrawable();
		}
	}
	

	public void addChild(GraphicEntity e){
		children.add(e);
		if(root==null){
			e.setRoot(this);
			if(Hub.view == this){
				e.onAddToDrawable();
			}
		}
		else {
			e.setRoot(root);
			if(Hub.view == root){
				e.onAddToDrawable();
			}
		}
	}
	public void removeChild(int e){
		if(e>=0){
			if(Hub.view == children.get(e).getRoot()){
				children.get(e).onRemoveFromDrawable();
			}
			children.remove(e).setRoot(null);
		}
	}
	public void removeChild(GraphicEntity e){
		removeChild(children.indexOf(e));
	}

	public void update(double secondsSinceLastFrame) {
		for(int i=0;i<children.size();++i){
			(children.get(i)).update(secondsSinceLastFrame);
		}
	}

	public void setVisible(boolean b) {
		for(int i=0;i<children.size();++i){
			children.get(i).setVisible(b);
		}
	}

	public void animate() {
		for(int i=0;i<children.size();++i){
			children.get(i).animate();
		}
	}

	@Override
	public boolean onClick(ClickEvent event) {		
		boolean found = false;
		for(int i=0;i<children.size();++i){
			if(children.get(i).onClick(event)){
				found = true;
			}
		}
		return found;
	}

	@Override
	public boolean onHover(HoverEvent event) {
		for(int i=0;i<children.size();++i){
			if(children.get(i).onHover(event)){
				return true;
			}
		}
		return false;
	}

	public void onMouseScroll(int distance){
		for(int i=0;i<children.size();++i){
			children.get(i).onMouseScroll(distance);
		}
	}
	public int size() {
		return children.size();
	}
	public GraphicEntity getChild(int i) {
		return children.get(i);
	}
	public List<GraphicEntity> getChildren() {
		return children;
	}
	public GraphicView getRoot() {
		return root;
	}
	public void setRoot(GraphicView graphicView) {
		for(GraphicEntity e:children){
			e.setRoot(graphicView);
		}
		this.root = graphicView;
	}
	public void turnOff() {
		for(int i=0;i<children.size();++i){
			children.get(i).turnOff();
		}		
	}
	public void turnOn(){
		for(int i=0;i<children.size();++i){
			children.get(i).turnOn();
		}
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		if(this instanceof KeyBoardListener){
			return (KeyBoardListener)this;
		}
		else return null;
	}

	public void exit(){
		
	}

}
