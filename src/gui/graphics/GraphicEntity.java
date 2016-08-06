package gui.graphics;

import main.Hub;

public class GraphicEntity extends GraphicView {
	protected GraphicElement entity;
	public GraphicEntity(String textureName, int layer) {
		super(false);
		this.entity = new GraphicElement(textureName,this);
		this.entity.setLayer(layer);
	}
	public GraphicEntity(String textureName) {
		this(textureName,0);
	}
	@Override
	public float getWidth(){
		return entity.getWidth();
	}
	@Override
	public float getHeight(){
		return entity.getHeight();
	}
	@Override
	public void adjust(float x, float y){
		entity.adjust(x, y);
		super.adjust(x, y);
	}
	@Override
	public void adjust(float x, float y, float dx, float dy){
		entity.adjust(x, y);
		super.adjust(x, y,dx,dy);
	}

	@Override
	public void setX(float x){
		entity.setX(x);
		super.setX(x);
	}

	@Override
	public void setY(float y){
		entity.setY(y);
		super.setY(y);
	}

	@Override
	public void onAddToDrawable(){
		if(Hub.currentView==parent){
			Hub.addLayer.add(entity);
		}
		entity.onAddToDrawable();
		super.onAddToDrawable();
	}

	@Override
	public void onRemoveFromDrawable(){
		if(Hub.currentView==parent){
			Hub.removeLayer.add(entity);
		}
		entity.onRemoveFromDrawable();
		super.onRemoveFromDrawable();

	}

	@Override
	public void draw(){
	}

	@Override
	public void animate(){
		entity.animate();
		super.animate();
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
	public void onDraw(){
		entity.onDraw();
		super.onDraw();
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
	@Override
	public void setFrame(int i) {
		entity.setFrame(i);
	}
	@Override
	public void rotate(float r) {
		entity.rotate((float) (r*360/2f/Math.PI));
	}

	public void setTextureName(String string) {
		this.entity.setTextureName(string);
	}
	@Override
	public String getTextureName() {
		return entity.getTextureName();
	}
	public GraphicElement getGraphicElement() {
		return entity;
	}
	@Override
	public int textureIndex(){
		return entity.textureIndex();
	}
	@Override
	public boolean isWithin(float dx, float dy) {
		return entity.isWithin(dx, dy);
	}
}
