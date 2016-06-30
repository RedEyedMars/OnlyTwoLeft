package gui.inputs;

public interface KeyBoardListener {
	public static final boolean DOWN = true;
	public static final boolean UP = false;
	public void keyCommand(boolean b,char c, int keycode);
	public boolean continuousKeyboard();
}
