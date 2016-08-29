package gui.graphics;

import java.util.ArrayList;
import java.util.List;

import gui.inputs.KeyBoardListener;
import gui.inputs.MotionEvent;
import gui.inputs.MouseListener;

public class GraphicView implements Graphicable, MouseListener{
	@SuppressWarnings("unused")
	private boolean temporary;
	protected List<GraphicEntity> children = new ArrayList<GraphicEntity>();
	protected float x=0f;
	protected float y=0f;
	private float height=1f;
	private float width=1f;
	protected GraphicView parent = null;
	protected boolean listenToRelease = false;
	public GraphicView(){
		this(false);
	}
	public GraphicView(boolean temporary){
		this.temporary = temporary;
	}

	public void update(double secondsSinceLastFrame) {
		for(int i=0;i<children.size();++i){
			(children.get(i)).update(secondsSinceLastFrame);
		}
	}

	@Override
	public void draw() {

	}

	@Override
	public int textureIndex() {
		return -1;
	}

	@Override
	public String getTextureName() {
		return null;
	}

	@Override
	public void setVisible(boolean b) {
		for(int i=0;i<children.size();++i){
			children.get(i).setVisible(b);
		}
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void animate() {
	}

	public float getX(){
		return x;
	}


	@Override
	public void setX(float x) {
		this.x = x;
		for(int i=0;i<children.size();++i){
			children.get(i).setX(x+offsetX(i));
		}
	}
	public float offsetX(int i){
		return 0;
	}
	public float getY(){
		return y;
	}

	@Override
	public void setY(float y) {
		this.y = y;
		for(int i=0;i<children.size();++i){
			children.get(i).setY(y+offsetY(i));
		}
	}

	public float offsetY(int i){
		return 0;
	}

	@Override
	public void adjust(float width, float height) {
		this.height = height;
		this.width = width;
		for(int i=0;i<children.size();++i){
			children.get(i).adjust(width,height);
		}
	}

	public void adjust(float width, float height, float dWidth, float dHeight){
		this.height = height;
		this.width = width;
		for(int i=0;i<children.size();++i){
			children.get(i).adjust(dWidth,dHeight);
		}
	}

	@Override
	public void onDraw() {

	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}
	@Override
	public void onAddToDrawable() {
		for(int i=0;i<children.size();++i){
			children.get(i).onAddToDrawable();
		}
	}

	@Override
	public void onRemoveFromDrawable() {
		while(!children.isEmpty()){
			removeChild(0);
		}
	}

	public void addChild(GraphicEntity e){
		children.add(e);
		e.setView(parent==null?this:parent);
	}
	public void removeChild(int e){
		children.get(e).onRemoveFromDrawable();
		children.remove(e).setView(null);
	}
	public void removeChild(GraphicEntity e){
		removeChild(children.indexOf(e));
	}


	@Override
	public boolean onClick(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN&&isWithin(event.getX(),event.getY())){
			boolean childrenHaveWithin = false;
			for(int i=0;i<children.size();++i){
				if(children.get(i).onClick(event)){
					childrenHaveWithin = true;
				}
			}
			if(!childrenHaveWithin){
				performOnClick(event);
			}
			return true;
		}
		else {
			if(listenToRelease&&event.getAction()==MotionEvent.ACTION_UP &&isWithin(event.getX(),event.getY())){
				for(int i=0;i<children.size();++i){
					children.get(i).onClick(event);
				}
				performOnRelease(event);
			}
			return false;
		}
	}

	@Override
	public boolean onHover(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN&&isWithin(event.getX(),event.getY())){
			boolean childrenHaveWithin = false;
			for(int i=0;i<children.size();++i){
				if(children.get(i).onHover(event)){
					childrenHaveWithin = true;
				}
			}
			if(!childrenHaveWithin){
				performOnHover(event);
			}
			return true;
		}
		else return false;
	}

	public void onMouseScroll(int distance){
		for(int i=0;i<children.size();++i){
			children.get(i).onMouseScroll(distance);
		}
	}

	public void performOnClick(MotionEvent event){		
	}
	public void performOnRelease(MotionEvent event){		
	}

	public void performOnHover(MotionEvent event){		
	}


	public boolean isWithin(float dx, float dy) {
		return dx>=getX()&&dx<=getX()+getWidth()&&
				dy>=getY()&&dy<=getY()+getHeight();
	}
	public int size() {
		return children.size();
	}
	public GraphicView getChild(int i) {
		return children.get(i);
	}
	public List<GraphicEntity> getChildren() {
		return children;
	}
	public GraphicView getView() {
		return parent;
	}
	public void setView(GraphicView graphicView) {
		for(GraphicEntity e:children){
			e.setView(graphicView);
		}
		this.parent = graphicView;
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
	public void listenToRelease(boolean b) {
		if(parent!=null){
			parent.listenToRelease(b);
		}
		listenToRelease = b;

	}
	public void setFrame(int i) {

	}
	public void rotate(float f) {
	}
	public KeyBoardListener getDefaultKeyBoardListener(){
		return null;
	}
	public void exit() {		
	}
}
