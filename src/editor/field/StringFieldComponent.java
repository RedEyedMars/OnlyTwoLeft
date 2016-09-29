package editor.field;

public abstract class StringFieldComponent <TargetType extends Object> extends TextFieldComponent<TargetType,String>{

	public StringFieldComponent(String font) {
		super(font);
	}
	protected void advance(String text){
		try {
			act(getText());
		}
		catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	protected boolean legalKey(char c, int keycode){
		return c>=65&&c<=90||c>=97&&c<=122||c==46||keycode==14||keycode==57;
	}
}
