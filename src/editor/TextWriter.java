package editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.KeyBoardListener;

public class TextWriter extends GraphicText implements KeyBoardListener {

	private int index = 0;
	private Map<Integer, ButtonAction> ctrlCommands;
	private Editor editor;
	public TextWriter(Editor parent,String text, Map<Integer,ButtonAction> ctrlCommands) {
		super(text);
		this.editor = parent;
		blinker.setVisible(true);
		this.ctrlCommands = ctrlCommands;
	}

	private boolean ctrling = false;
	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if(ctrling){
				if(ctrlCommands.containsKey(keycode)){
					ctrlCommands.get(keycode).act(editor);
				}
			}
			else if(keycode==29||keycode==157){
				ctrling=true;
			}
			else if(keycode==14){
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
			else if(keycode==28){
				insert('\n');
				charIndex=0;
				++lineIndex;

			}
			else if(keycode==207){//end

			}
			else if(keycode==211){//delete

			}
			else if(keycode==200){//up
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
			else if(keycode==203){//left
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
			else if(keycode==208){//down
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
			else if(keycode==205){//right
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
			else if(c>0){
				insert(c);
			}
		}
		else if(KeyBoardListener.UP==b){
			if(keycode==29||keycode==157){
				ctrling=false;
			}
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
	
	public String[] getLines(){
		List<String> strings = new ArrayList<String>();
		for(GraphicLine line:lines){
			strings.add(line.getText());
		}
		return strings.toArray(new String[0]);
	}

}
