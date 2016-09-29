package editor.program;

public interface Settable {
	public String[] copiableIntTextureNames();
	public int[] copiableIntTextureRanges();
	
	public String[] copiableIntNames();
	public String[] copiableValueNames();
	
	public Integer[] copiableIntIds();
	public Integer[] copiableValueIds();
	
	public void setValue(int index, float value);
	public void setValue(int index, int value);
	
	public float getValue(int index);
	public int getInt(int index);
}
