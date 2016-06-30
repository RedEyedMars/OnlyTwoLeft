package gui.graphics;


public interface Graphicable{


	public void draw();
	public int textureIndex();
	public String getTextureName();
	public void setVisible(boolean b);
	public boolean isVisible();
	public void onAddToDrawable();
	public void onRemoveFromDrawable();
	public void animate();
	public void setX(float x);
	public void setY(float y);
	public void adjust(float width, float height);
	public void onDraw();
	public float getWidth();
	public float getHeight();
}
