package gui.inputs;



public interface MouseListener {
	public boolean onClick(MotionEvent event);
	public boolean onHover(MotionEvent event);
	public void onMouseScroll(int distance);
}
