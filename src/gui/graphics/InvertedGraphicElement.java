package gui.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import main.Hub;

import org.lwjgl.opengl.GL11;

import gui.inputs.MotionEvent;



public class InvertedGraphicElement extends GraphicElement{
	
	public InvertedGraphicElement(String textureName, GraphicView view) {
		super(textureName, view);
	}
	public void draw() {
		if(isVisible()&&on)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(transX, transY, 0.0f);
			GL11.glRotatef(angle-90, 0, 0, 1);
			GL11.glScalef(height, width, 1f);
			GL11.glVertexPointer(3, 0, vertexBuffer);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
			GL11.glPopMatrix();

			if(Hub.renderer.animate){
				animate();
			}
			onDraw();
		}
	}
	
	public void adjust(float x, float y) {
		width = x;
		height = y;
	}

		public float getHeight(){
		return width;
	}
	public float getWidth(){
		return height;
	}
}
