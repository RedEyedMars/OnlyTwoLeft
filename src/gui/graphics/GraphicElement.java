package gui.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import main.Hub;

import org.lwjgl.opengl.GL11;

import gui.inputs.MotionEvent;



public class GraphicElement implements Graphicable{
	public static final int SQUARE=0;
	public static final int TRIANGLE=1;
	public static final int HEXAGON=2;
	protected String texName;
	protected float x = 0f;
	protected float y = 0f;
	protected float angle = 0.0f;
	protected static FloatBuffer squareBuffer;
	protected static FloatBuffer triangleBuffer;
	protected static FloatBuffer trTriangleBuffer;
	protected static FloatBuffer tlTriangleBuffer;
	protected static FloatBuffer brTriangleBuffer;
	protected static FloatBuffer blTriangleBuffer;	
	protected static FloatBuffer hexagonBuffer;
	protected FloatBuffer vertexBuffer;
	protected int vertexNumber = 4;
	protected boolean isVisible = true;
	protected boolean on = true;
	protected int textureIndex = 0;
	protected float width = 1f;
	protected float height = 1f;
	protected int layer = 0;
	
	protected float visualX = 0f;
	protected float visualY = 0f;
	protected float visualW = 0f;
	protected float visualH = 0f;

	private GraphicView view;
	public GraphicElement(String textureName, GraphicView view) {
		this(textureName,view,1f,1f);
	}
	public GraphicElement(String textureName, GraphicView view, float f, float g) {
		this.view = view;
		adjust(width,height);
		setTextureName(textureName);
		this.vertexBuffer=squareBuffer;
	}
	public void onDraw(){
		
	}
	static {
		setupVertices();
	}
	protected static void setupVertices(){
		float[] vertices = new float[]{
				0f, 0f,  0.0f,		// V1 - bottom left
				0f,  1f,  0.0f,		// V2 - top left
				1f, 0f,  0.0f,		// V3 - bottom right
				1f,  1f,  0.0f,		// V4 - top right

		};

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		squareBuffer = byteBuffer.asFloatBuffer();
		squareBuffer.clear();
		squareBuffer.put(vertices);
		squareBuffer.position(0);
		
		vertices = new float[]{
				0f, 0f,  0.0f,		// V1 - bottom left
				0.5f,  1f,  0.0f,		// V2 - top left
				1f, 0f,  0.0f,		// V3 - bottom right

		};

		byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		triangleBuffer = byteBuffer.asFloatBuffer();
		triangleBuffer.clear();
		triangleBuffer.put(vertices);
		triangleBuffer.position(0);
		
		//topleft
		vertices = new float[]{
				0f, 0f,  0.0f,		// V1 - bottom left
				0f,  1f,  0.0f,		// V2 - top left
				1f,  1f,  0.0f,		// V4 - top right

		};

		byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		tlTriangleBuffer = byteBuffer.asFloatBuffer();
		tlTriangleBuffer.clear();
		tlTriangleBuffer.put(vertices);
		tlTriangleBuffer.position(0);
		//topright
		vertices = new float[]{

				0f, 1f,  0.0f,		// V1 - bottom left
				1f,  1f,  0.0f,		// V4 - top right
				1f, 0f,  0.0f,		// V3 - bottom right

		};

		byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		trTriangleBuffer = byteBuffer.asFloatBuffer();
		trTriangleBuffer.clear();
		trTriangleBuffer.put(vertices);
		trTriangleBuffer.position(0);
		
		//bottomleft
		vertices = new float[]{
				0f, 0f,  0.0f,		// V1 - bottom left
				0f,  1f,  0.0f,		// V2 - top left
				1f,  0f,  0.0f,		// V4 - top right

		};

		byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		blTriangleBuffer = byteBuffer.asFloatBuffer();
		blTriangleBuffer.clear();
		blTriangleBuffer.put(vertices);
		blTriangleBuffer.position(0);
		
		//bottomright
		vertices = new float[]{
				0f, 0f,  0.0f,
				1f, 1f,  0.0f,
				1f,  0f,  0.0f,

		};

		byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		brTriangleBuffer = byteBuffer.asFloatBuffer();
		brTriangleBuffer.clear();
		brTriangleBuffer.put(vertices);
		brTriangleBuffer.position(0);
		/*
		 * 
  3----5
 /|\   |\
1 | \  | 6
 \|  \ |/
  2----4
		 */
		vertices = new float[]{
				0f, 0.5f,  0.0f,
				0.2f,1f,  0.0f,
				0.2f,0f,  0.0f,
				0.8f,1f,  0.0f,
				0.8f,0f,  0.0f,
				1f,0.5f,  0.0f
		};

		byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		hexagonBuffer = byteBuffer.asFloatBuffer();
		hexagonBuffer.clear();
		hexagonBuffer.put(vertices);
		hexagonBuffer.position(0);
	}
	/*B00BIES2 
	BHaskic0322 or
	5832201BH*/
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
			GL11.glTranslatef(visualX, visualY, 0.0f);
			if(angle!=0f){
				GL11.glTranslatef(visualW/2f, visualH/2f, 0.0f);
				GL11.glRotatef(angle, 0, 0, 1);	
				GL11.glTranslatef(-visualW/2f, -visualH/2f, 0.0f);
			}
			GL11.glScalef(visualW, visualH, 1f);
			GL11.glVertexPointer(3, 0, vertexBuffer);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, vertexNumber);
			GL11.glPopMatrix();

			if(Hub.renderer.animate){
				view.animate();
			}
			onDraw();
		}
	}
	public void setX(float x){
		this.x = x;
		if(width<0){
			visualX= x+width;
		}
		else {
			visualX = x;
		}
	}
	public void setY(float y){
		this.y = y;
		if(height<0){
			visualY= y+height;
		}
		else {
			visualY = y;
		}
	}
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
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
	public void adjust(float w, float h) {
		width = w;
		if(width<0){
			visualX= x+width;
			visualW = -width;
		}
		else {
			visualW = width;
		}
		height = h;
		if(height<0){
			visualY= y+height;
			visualH = -height;
		}
		else {
			visualH = height;
		}
		
	}

	@Override
	public void onAddToDrawable() {
	}

	@Override
	public void onRemoveFromDrawable() {
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
		return dx>=visualX&&dx<visualX+visualW&&
				dy>=visualY&&dy<visualY+visualH;
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
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer){
		this.layer = layer;
	}
	public float getAngle() {
		return angle;
	}
	public void setShape(int i){
		switch(i){
		case 0:{
			vertexBuffer=squareBuffer;
			vertexNumber=4;
			break;
		}
		case 1:{
			vertexBuffer=triangleBuffer;
			vertexNumber=3;
			break;
		}
		case 2:{
			vertexBuffer=tlTriangleBuffer;
			vertexNumber=3;
			break;
		}
		case 3:{
			vertexBuffer=trTriangleBuffer;
			vertexNumber=3;
			break;
		}
		case 4:{
			vertexBuffer=blTriangleBuffer;
			vertexNumber=3;
			break;
		}
		case 5:{
			vertexBuffer=brTriangleBuffer;
			vertexNumber=3;
			break;
		}
		case 6:{
			vertexBuffer=hexagonBuffer;
			vertexNumber=6;
			break;
		}
		}
	}
	public int getReflectedShape() {
		if(vertexBuffer==brTriangleBuffer){
			return 2;
		}
		else if(vertexBuffer==blTriangleBuffer){
			return 3;
		}
		else if(vertexBuffer==trTriangleBuffer){
			return 4;
		}
		else if(vertexBuffer==tlTriangleBuffer){
			return 5;
		}
		return -1;
	}

}
