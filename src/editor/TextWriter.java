package editor;

import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.inputs.KeyBoardListener;

public class TextWriter extends GraphicText implements KeyBoardListener {

	private int index = 0;
	public TextWriter(String text) {
		super(text);
		blinker.setVisible(true);
	}

	@Override
	public void keyCommand(boolean b, char c, int keycode) {
		if(b==KeyBoardListener.DOWN){
			if(keycode==14){
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
					index-=
							charIndex>=lines.get(lineIndex).length()?
									lines.get(lineIndex).length():
										charIndex;	
									--lineIndex;
									index-=lines.get(lineIndex).length()+1;
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

}
