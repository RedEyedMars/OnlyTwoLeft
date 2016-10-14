package editor.program;

public interface Settable {
	public static final int UNKNOWN = -1;
	public static final int FLOAT = 0;
	public static final int STRING = 1;
	public String[] copiableIntTextureNames();
	public int[] copiableIntTextureRanges();
	
	public String[] copiableIntNames();
	public String[] copiableValueNames();
	
	public Integer[] copiableIntIds();
	public Integer[] copiableValueIds();

	public void setValue(int index, String value);
	public void setValue(int index, float value);
	public void setValue(int index, int value);

	public String getStringValue(int index);
	public float getValue(int index);
	public int getInt(int index);
	
	public int getValueType(int index);
}
