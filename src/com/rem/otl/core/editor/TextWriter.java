package com.rem.otl.core.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.KeyBoardListener;
import com.rem.otl.core.main.Hub;

public class TextWriter extends GraphicText implements KeyBoardListener {

	protected int index = 0;
	protected Map<Integer, ButtonAction> ctrlCommands;
	private Editor editor;
	protected boolean ctrling = false;
	public TextWriter(Editor parent,String text, Map<Integer,ButtonAction> ctrlCommands) {
		super("timesnewroman",text,0);
		this.editor = parent;
		blinker.turnOn();
		this.ctrlCommands = ctrlCommands;
	}
	public TextWriter(String font,String text) {
		super(font,text,Hub.MID_LAYER);
		this.editor = null;
	}
	
	public void turnBlinkerOn(){
		blinker.turnOn();
	}
	public void turnBlinkerOff(){
		blinker.turnOff();
	}

	@Override
	public void onType(KeyBoardEvent event) {
		if(event.keyDown()){
			if(ctrling){
				
			}
			else if(event.is(KeyBoardEvent.CTRL_LEFT)||event.is(KeyBoardEvent.CTRL_RIGHT)){
				ctrling=true;
			}
			else if(event.is(KeyBoardEvent.BACKSPACE)){
				if(getText().length()==0)return;
				if(lineIndex>0&&getText().charAt(index-1)=='\n'){
					--lineIndex;
					charIndex=lines.get(lineIndex).length()+1;
				}

				if(index>0){
					//Delete:change(getText().substring(1, getText().length()));
					if(index<getText().length()){
						change(getText().substring(0, index-1)+getText().substring(index));
						--index;
						--charIndex;
					}
					else {
						change(getText().substring(0, index-1));
						--index;
						--charIndex;
					}
				}

				if(charIndex>lines.get(lineIndex).length()){
					charIndex=lines.get(lineIndex).length();
				}
			}
			else if(event.is(KeyBoardEvent.ENTER)){
				insert('\n');
				charIndex=0;
				++lineIndex;

			}
			else if(event.is(KeyBoardEvent.END)){//end
				
			}
			else if(event.is(KeyBoardEvent.DELETE)){//delete
				if(getText().length()==0)return;
				if(index>0){
					if(index<getText().length()){
						change(getText().substring(0, index)+getText().substring(index+1));
					}					
				}
				else {
					change(getText().substring(1));
				}
			}
			else if(event.is(KeyBoardEvent.UP)){//up
				if(lineIndex>0){
					if(lineIndex<lines.size()){
						index-=
							charIndex>=lines.get(lineIndex).length()?
									lines.get(lineIndex).length():
									charIndex;
						--index;
					}
					
							--lineIndex;
							index-=lines.get(lineIndex).length();
							index+=
									charIndex>=lines.get(lineIndex).length()?
									lines.get(lineIndex).length():
									charIndex;				

				}
			}
			else if(event.is(KeyBoardEvent.LEFT)){//left
				if(index>0){
					--index;
					if(charIndex>0){				
						--charIndex;
					}
					else {
						--lineIndex;
						charIndex=lines.get(lineIndex).length();
					}
				}

			}
			else if(event.is(KeyBoardEvent.DOWN)){//down
				if(lineIndex<lines.size()-1){
					index+=
							charIndex>=lines.get(lineIndex).length()?
									1:
										lines.get(lineIndex).length()-charIndex+1;	
							++lineIndex;
							index+=
									charIndex>=lines.get(lineIndex).length()?
											lines.get(lineIndex).length():
												charIndex;

				}
			}
			else if(event.is(KeyBoardEvent.RIGHT)){//right
				if(index<text.length()){
					++index;
					if(charIndex<lines.get(lineIndex).length()){				
						++charIndex;
					}
					else if(lineIndex<lines.size()-1){
						++lineIndex;
						charIndex=0;
					}
				}
			}
			else if(event.getChar()>0){
				insert(event.getChar());
			}
		}
		else if(event.keyUp()){
			if(event.is(KeyBoardEvent.CTRL_LEFT)||event.is(KeyBoardEvent.CTRL_RIGHT)){
				ctrling=false;
			}
			else if(ctrling){
				if(ctrlCommands.containsKey(event.getKeyCode())){
					ctrlCommands.get(event.getKeyCode()).act(null);
				}
			}
		}

	}
	
	public void changeTextOnLine(String text, int line){
		if(line<lines.size()){
			index =0;
			for(int i=0;i<line;++i){
				index+=lines.get(i).length();
			}
			index+=line;
			if(index<text.length()){
				if(index==0){
					change(text+getText().substring(index+lines.get(line).length()));
				}
				else {

					change(getText().substring(0, index)+text+getText().substring(index+lines.get(line).length()));
				}
			}
			else {
				change(getText().substring(0, lines.get(line).length())+text);
			}
			charIndex=lines.get(line).length();
			lineIndex=line;
			index+=text.length();
		}
	}

	private void insert(char c) {
		if(index<text.length()){
			if(index==0){
				change(c+getText());
				++index;
				++charIndex;
			}
			else {
				change(getText().substring(0, index)+c+getText().substring(index));
				++index;
				++charIndex;
			}
		}
		else {
			change(getText()+c);
			++index;
			++charIndex;
		}
	}

	@Override
	public boolean continuousKeyboard() {
		return false;
	}
	
	public void clearText(){
		this.index = 0;
		this.charIndex = 0;
		this.lineIndex = 0;
		this.change("");
	}
	
	public String[] getLines(){
		List<String> strings = new ArrayList<String>();
		for(GraphicLine line:lines){
			strings.add(line.getText());
		}
		return strings.toArray(new String[0]);
	}
	public String getTextOnLine() {
		return lines.get(this.lineIndex).getText();
	}
	public void wrap(float maxLineLength) {
		wrap(0, maxLineLength);

		StringBuilder builder = new StringBuilder();
		String nl = "";
		for(GraphicLine line:lines){
			builder.append(nl);
			builder.append(line.getText());
			nl = "\n";
		}
		change(builder.toString());
	}
	private void wrap(int li, float max){
		if(li>=lines.size())return;
		String text = lines.get(li).getText();
		String excess = lines.get(li).wrap(max);
		if(excess.length()>0){
			if(lineIndex == li&&charIndex==text.length()){
				++index;
				charIndex=1;
				++lineIndex;
			}
			getLine(li+1).change(excess+getLine(li+1).getText());			
		}
		wrap(li+1,max);
	}

}
