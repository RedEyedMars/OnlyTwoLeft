package gui.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class GraphicLine extends GraphicEntity{

	private static final int SEGMENT_COUNT = 10;
	public GraphicLine(int layer) {
		super("squares", layer);
		this.entity = new GraphicElement("blank", this){
			@Override
			public void draw(){

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				// OpenGL 1.1 drawing routine
				GL11.glLineWidth(2);
				GL11.glColor3f(1f, 0f, 0f);
				GL11.glColor3f(0.5f,0.5f,1.0f);
				GL11.glBegin(GL11.GL_LINES);
				
				Point p0 = new Point(getX(),getY());
				Point p1 = new Point(getX(),getY()+getHeight()*3f/4f);
				Point p2 = new Point(getX()+getWidth()*3f/4f,getY()+getHeight());
				Point p3 = new Point(getX()+getWidth(),getY()+getHeight());
				Point q0 = calculateBezierPoint(0, p0, p1, p2, p3);
				 
				for(int i = 1; i <= SEGMENT_COUNT; i++)
				{
				  float t = i / (float) SEGMENT_COUNT;
				  Point q1 = calculateBezierPoint(t, p0, p1, p2, p3);

					GL11.glVertex3f(q0.x,  q0.y, 0.0f);
					GL11.glVertex3f(q1.x,  q1.y,  0.0f);
				  q0 = q1;
				}
				GL11.glEnd();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		};
		this.entity.setLayer(layer);
	}

	private class Point {

		public float x;
		public float y;
		public Point(float x, float y){
			this.x = x;
			this.y = y;
		}

	}
	private Point calculateBezierPoint(float t,
			Point p0, Point p1, Point p2, Point p3)
	{
		float u = 1 - t;
		float tt = t*t;
		float uu = u*u;
		float uuu = uu * u;
		float ttt = tt * t;

		//first term
		Point p = new Point(uuu * p0.x,uuu * p0.y);
		//second term
		p.x += 3 * uu * t * p1.x;
		p.y += 3 * uu * t * p1.y;
		//third term
		p.x += 3 * u * tt * p2.x;
		p.y += 3 * u * tt * p2.y;
		//fourth term
		p.x += ttt * p3.x;
		p.y += ttt * p3.y;

		return p;
	}
}
