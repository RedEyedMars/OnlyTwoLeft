package game;

import java.util.ArrayList;
import java.util.List;

import editor.TextWriter;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.KeyBoardListener;
import main.Hub;

public class Chat extends GraphicEntity implements KeyBoardListener {

	private List<String> log = new ArrayList<String>();
	private GraphicText logDisplay;
	private int scrollIndex = 0;
	private TextWriter writer;
	private static final int LOG_LENGTH = 16;
	private static final int MAX_LINE_LENGTH = 18;
	public Chat(int layer) {
		super("chatbox", Hub.MID_LAYER);
		logDisplay = new GraphicText("impact","",Hub.MID_LAYER);
		writer = new TextWriter("impact","");

		writer.turnBlinkerOff();

		addChild(logDisplay);
		addChild(writer);

		resize(0.3f,0.6f);
		reposition(getX(),getY());
	}
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(keycode==28&&b==KeyBoardListener.UP){
			//setVisible(false);
			String[] lines = writer.getText().split("\n");
			boolean first = true;
			for(String line:lines){
				if(first){
					log.add(">"+line);
					first = false;
				}
				else {
					log.add(line);
				}
			}
			scrollIndex = log.size()-LOG_LENGTH;
			if(scrollIndex<0)scrollIndex=0;
			updateLog();
			writer.clearText();
			Gui.removeOnType(this.getDefaultKeyBoardListener());
			writer.turnBlinkerOff();
		}
		else if(keycode!=28){
			writer.keyCommand(b, c, keycode);
			writer.wrap(0.2f);//MAX_LINE_LENGTH);
		}
	}
	@Override
	public boolean continuousKeyboard() {
		return writer.continuousKeyboard();
	}
	@Override
	public float offsetY(int index){
		if(getChild(index)==writer){
			return 0.075f;
		}
		else if(getChild(index)==logDisplay){
			if(log.size()<LOG_LENGTH ){
				return log.size()*0.025f+0.0825f;
			}
			else {
				return LOG_LENGTH*0.025f+0.0825f;
			}
		}
		else return offsetY(index);
	}
	public void updateLog(){
		if(log.size()<LOG_LENGTH ){
			StringBuilder builder = new StringBuilder();
			for(String line:log){
				builder.append(line);
				builder.append('\n');
			}
			logDisplay.change(builder.toString());
		}
		else {
			StringBuilder builder = new StringBuilder();
			for(int i=scrollIndex;i<scrollIndex+LOG_LENGTH&&i<log.size();++i){
				builder.append(log.get(i));
				builder.append('\n');
			}
			logDisplay.change(builder.toString());
		}

		reposition(getX(),getY());
	}
	public void blinkerOn() {
		writer.turnBlinkerOn();
	}
}
