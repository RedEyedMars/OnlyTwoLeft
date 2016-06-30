package gui.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import main.Hub;

import org.lwjgl.opengl.GL11;

import gui.inputs.MotionEvent;



public class GraphicElement implements Graphicable{
	protected float angle = 0.0f;
	protected String texName;
	protected float transX = 0f;
	protected float transY = 0f;
	protected float vertices[];
	protected FloatBuffer vertexBuffer;
	protected boolean isVisible = true;
	protected boolean on = true;
	protected int textureIndex = 0;
	protected float width = 1f;
	protected float height = 1f;
	protected int layer = 0;
	private boolean xPlaneRotated = false;

	private GraphicView view;
	public GraphicElement(String textureName, GraphicView view) {
		this(textureName,view,1f,1f);
	}
	public GraphicElement(String textureName, GraphicView view, float f, float g) {
		this.view = view;
		setupVertices();
		adjust(width,height);
		setTextureName(textureName);
	}
	public void onDraw(){
		
	}
	protected void setupVertices(){
		vertices = new float[]{
				0f, 0f,  0.0f,		// V1 - bottom left
				0f,  1f,  0.0f,		// V2 - top left
				1f, 0f,  0.0f,		// V3 - bottom right
				1f,  1f,  0.0f,		// V4 - top right

		};

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuffer.asFloatBuffer();
		vertexBuffer.clear();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
	}
	public void setTextureName(String n){
		texName = n;
	}

	public String getTextureName() {
		return texName;
	}

	public void draw() {
		if(isVisible()&&on)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(transX, transY, 0.0f);
			GL11.glRotatef(angle, 0, 0, 1);
			GL11.glScalef(width, height, 1f);
			GL11.glVertexPointer(3, 0, vertexBuffer);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
			GL11.glPopMatrix();

			if(Hub.renderer.animate){
				view.animate();
			}
			onDraw();
		}
	}
	public void setX(float x){
		transX = x;
	}
	public void setY(float y){
		transY = y;
	}
	public float getX(){
		return transX;
	}
	public float getY(){
		return transY;
	}
	public void translate(float x, float y){
		transX += x;
		transY += y;
	}
	public void rotate(float r){
		angle = r;
	}
	@Override
	public boolean isVisible(){
		return isVisible;
	}
	@Override
	public int textureIndex(){
		return textureIndex ;
	}
	public void adjust(float x, float y) {
		width = x;
		height = y;
	}

	@Override
	public void onAddToDrawable() {
	}

	@Override
	public void onRemoveFromDrawable() {
		Hub.removeLayer.add(this);
	}
	public float getHeight(){
		return height;
	}
	public float getWidth(){
		return width;
	}

	public void setFrame(int imageIndex) {
		textureIndex = imageIndex;
	}

	public boolean isWithin(float dx, float dy) {
		return dx>getX()&&dx<getX()+getWidth()&&
				dy>getY()&&dy<getY()+getHeight();
	}

	public void setVisible(boolean b) {
		this.isVisible = b;
	}
	@Override
	public void animate() {		
	}
	
	public void on(boolean b) {
		this.on = b;
	}

}
