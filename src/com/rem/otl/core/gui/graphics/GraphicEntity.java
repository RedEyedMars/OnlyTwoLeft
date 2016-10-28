package com.rem.otl.core.gui.graphics;

import com.rem.otl.core.gui.inputs.HoverEvent;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class GraphicEntity extends GraphicView {
	protected GraphicElement entity;
	protected GraphicView root = null;
	protected Animation<GraphicEntity> animation = null;
	public GraphicEntity(String textureName, int layer) {
		super();
		this.entity = Hub.creator.createGraphicElement(textureName,this);
		this.entity.setLayer(layer);
	}
	public GraphicEntity(String textureName) {
		this(textureName,Hub.BOT_LAYER);
	}
	public float getX(){
		return entity.getX();
	}
	public float getY(){
		return entity.getY();
	}
	public float getWidth(){
		return entity.getWidth();
	}
	public float getHeight(){
		return entity.getHeight();
	}
	@Override
	public void resize(float x, float y){
		entity.resize(x, y);
		super.resize(x, y);
	}
	@Override
	public void resize(float x, float y, float dx, float dy){
		entity.resize(x, y);
		super.resize(x, y,dx,dy);
	}

	@Override
	public void reposition(float x, float y){
		entity.setX(x);
		entity.setY(y);
		super.reposition(x,y);
	}

	@Override
	public void onAddToDrawable(){
		Hub.renderer.addElement(entity);
		super.onAddToDrawable();
	}

	@Override
	public void onRemoveFromDrawable(){
		Hub.renderer.removeElement(entity);
		super.onRemoveFromDrawable();

	}
	
	@Override
	public void animate(){
		if(animation!=null){
			animation.onAnimate(this);
		}
		super.animate();
	}
	public void setAnimation(Animation<GraphicEntity> animation){
		this.animation = animation;
	}
	@Override
	public void setVisible(boolean vis){
		entity.setVisible(vis);
		super.setVisible(vis);
	}
	@Override
	public boolean isVisible(){
		return entity.isVisible();
	}	

	@Override
	public void turnOff() {
		entity.on(false);		
		super.turnOff();
	}
	@Override
	public void turnOn(){
		entity.on(true);
		super.turnOn();
	}
	public void setFrame(int i) {
		entity.setFrame(i);
	}
	public int getFrame(){
		return entity.getFrame();
	}
	public void rotate(float r) {
		entity.rotate((float) (r*360/2f/Math.PI));
	}

	public void setTextureName(String string) {
		this.entity.setTextureName(string);
	}
	public String getTextureName() {
		return entity.getTextureName();
	}
	public GraphicElement getGraphicElement() {
		return entity;
	}
	public boolean isWithin(float dx, float dy) {
		return entity.isWithin(dx, dy);
	}
	public void setShape(int i){
		entity.setShape(i);
	}
	@Override
	public boolean onClick(ClickEvent event) {
		if(this.isWithin(event.getX(), event.getY())){
			super.onClick(event);
			if(event.getAction()==ClickEvent.ACTION_DOWN){
				this.performOnClick(event);
			}
			else if(event.getAction()==ClickEvent.ACTION_UP){
				this.performOnRelease(event);
			}
			return true;
		}
		else return super.onClick(event);
	}

	@Override
	public boolean onHover(HoverEvent event) {
		if(this.isWithin(event.getX(), event.getY())){
			this.performOnHover(event);
			return true;
		}
		else return super.onHover(event);
	}

	public void performOnClick(ClickEvent event){		
	}
	public void performOnRelease(ClickEvent event){		
	}

	public void performOnHover(HoverEvent event){		
	}
	public void setLayer(int layer) {
		this.entity.setLayer(layer);
		for(GraphicEntity entity:children){
			entity.setLayer(layer);
		}
	}
}
