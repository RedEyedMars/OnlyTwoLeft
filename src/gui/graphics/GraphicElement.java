package gui.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import main.Hub;

import org.lwjgl.opengl.GL11;



public class GraphicElement {
	protected static FloatBuffer squareBuffer;
	protected static FloatBuffer triangleBuffer;
	protected static FloatBuffer trTriangleBuffer;
	protected static FloatBuffer tlTriangleBuffer;
	protected static FloatBuffer brTriangleBuffer;
	protected static FloatBuffer blTriangleBuffer;	
	protected static FloatBuffer hexagonBuffer;

	protected float x = 0f;
	protected float y = 0f;
	protected float width = 1f;
	protected float height = 1f;
	protected float angle = 0.0f;
	
	protected int vertexNumber = 4;
	protected FloatBuffer vertexBuffer;

	protected String texName;
	protected int frame = 0;
	protected int layer = 0;

	protected boolean isVisible = true;
	protected boolean on = true;
	
	protected float visualX = 0f;
	protected float visualY = 0f;
	protected float visualW = 0f;
	protected float visualH = 0f;

	private GraphicView view;

	public GraphicElement(String textureName, GraphicView view) {
		this.view = view;
		resize(1f,1f);
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
	public boolean isVisible(){
		return isVisible;
	}
	public void resize(float w, float h) {
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

	public float getHeight(){
		return height;
	}
	public float getWidth(){
		return width;
	}

	public int getFrame(){
		return frame ;
	}
	public void setFrame(int frame) {
		this.frame = frame;
	}

	public boolean isWithin(float dx, float dy) {
		return dx>=visualX&&dx<visualX+visualW&&
				dy>=visualY&&dy<visualY+visualH;
	}

	public void setVisible(boolean b) {
		this.isVisible = b;
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
	public static String getShapeName(int i) {
		switch(i){
		case 0:{
			return "Square";
		}
		case 1:{
			return "Regular Triangle";
		}
		case 2:{
			return "Right Triangle(point at the top left)";
		}
		case 3:{
			return "Right Triangle(point at the top right)";
		}
		case 4:{
			return "Right Triangle(point at the bottom left)";
		}
		case 5:{
			return "Right Triangle(point at the bottom right)";
		}
		case 6:{
			return "Hexagon";
		}
		}
		return null;
	}

}
