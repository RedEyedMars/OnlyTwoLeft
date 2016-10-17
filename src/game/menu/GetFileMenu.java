package game.menu;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import editor.TextWriter;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicText;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import main.Hub;

public class GetFileMenu extends Menu{

	protected static final int NEW_FOLDER = 0;
	protected static final int NEW_FILE = 1;
	private String extension = "";
	private File foundFile;
	private Stack<File> filePath = new Stack<File>();
	private boolean finished=false;
	private File[] innerFiles;
	private MenuButton backButton;
	private MenuButton returnButton;
	private MenuButton[] buttons = new MenuButton[]{null,null,null,null};
	private File currentFolder;
	private MotionEvent served = null;
	private MenuButton downButton;
	private MenuButton upButton;
	private GraphicEntity scrollBar;
	private int scrollOffset = 0;

	private TextWriter createNewNameWriter;
	private int creatingNew;

	public GetFileMenu(GraphicView parentView,final String startFolder, boolean canCreateNew) {
		super();
		this.currentFolder = new File("data"+File.separator+startFolder);
		this.extension = startFolder.substring(0,3);
		innerFiles = currentFolder.listFiles();
		for(int i=0;i<4;++i){
			final int fileIndex = i;
			buttons[i] = new MenuButton(trimExtension(innerFiles[i].getName())){
				@Override
				public void performOnRelease(MotionEvent e){
					if(e!=served&&this.isVisible()){
						if(innerFiles[fileIndex+scrollOffset].isDirectory()){
							filePath.push(currentFolder);
							changeButtons(innerFiles[fileIndex+scrollOffset]);
						}
						else {
							setFile(innerFiles[fileIndex+scrollOffset]);
						}
						served = e;
					}
				}

			};
			buttons[i].resize(0.6f, 0.15f);
			buttons[i].reposition(0.2f, 0.67f-0.16f*i);
			addChild(buttons[i]);
		}
		backButton = new IconMenuButton("editor_arrows",3){
			@Override
			public void performOnRelease(MotionEvent e){
				if(!filePath.isEmpty()){
					changeButtons(filePath.pop());
				}
			}
		};
		backButton.resize(0.15f, 0.15f);
		backButton.reposition(0.025f, 0.67f);
		addChild(backButton);
		backButton.setVisible(false);
		if(canCreateNew){
			final MenuButton createNewNameField = new MenuButton(""){

				@Override
				public void performOnRelease(MotionEvent e){
					if(this.isVisible()){
						if(createNewNameWriter!=null&&
								!("."+startFolder.substring(0,3)).equals(createNewNameWriter.getText())&&
								!"".equals(createNewNameWriter.getText())){

							File file = new File(currentFolder.getAbsolutePath()+
									File.separator+createNewNameWriter.getText());
							if(!file.exists()){
								Gui.removeOnType(createNewNameWriter);
								if(creatingNew == NEW_FILE){
									setFile(file);
								}
								else if(creatingNew == NEW_FOLDER){
									if(file.mkdirs()){
										filePath.push(currentFolder);
										changeButtons(file);
										this.setVisible(false);
										createNewNameWriter.setVisible(false);
									}
								}
							}

						}
					}
				}
			};
			createNewNameWriter = new TextWriter("impact","."+startFolder.substring(0,3)){
				{
					this.charIndex = 0;
					this.index = 0;
					setFontSize(GraphicText.FONT_SIZE_LARGE);
					resize(getWidth(), getHeight());
					reposition(0f,0f);
				}
				@Override
				public void keyCommand(boolean b, char c, int keycode){
					if((c>=32&&c<=127)||keycode==14){
						super.keyCommand(b, c, keycode);
					}
					else if(keycode==28){
						Hub.currentView.onClick(
								new MotionEvent(
										createNewNameField.getX()+createNewNameField.getWidth()/2f,
										createNewNameField.getY()+createNewNameField.getHeight()/2f,
										MotionEvent.ACTION_UP,MotionEvent.MOUSE_LEFT));
					}
				}
			};

			createNewNameField.resize(0.6f, 0.15f);
			createNewNameField.reposition(0.2f, 0.83f);
			addChild(createNewNameField);
			createNewNameField.setVisible(false);

			createNewNameWriter.resize(0.6f, 0.18f);
			createNewNameWriter.reposition(0.23f, 0.83f);
			addChild(createNewNameWriter);
			createNewNameWriter.setVisible(false);
			IconMenuButton createNewFileButton = new IconMenuButton("editor_button",3){
				@Override
				public void performOnRelease(MotionEvent e){
					setVisible(false);
					createNewNameField.setVisible(true);
					createNewNameWriter.setVisible(true);
					Gui.giveOnType(createNewNameWriter);
					creatingNew = NEW_FILE;
				}
			};
			createNewFileButton.resize(0.15f, 0.075f);
			createNewFileButton.reposition(0.82f, 0.895f);
			addChild(createNewFileButton);

			IconMenuButton createNewFolderButton = new IconMenuButton("editor_button",7){
				@Override
				public void performOnRelease(MotionEvent e){
					createNewNameField.setVisible(true);
					createNewNameWriter.setVisible(true);
					Gui.giveOnType(createNewNameWriter);
					creatingNew = NEW_FOLDER;
					createNewNameWriter.change("");
				}
			};
			createNewFolderButton.resize(0.15f, 0.075f);
			createNewFolderButton.reposition(0.82f, 0.82f);
			addChild(createNewFolderButton);
		}

		scrollBar = new GraphicEntity("squares",Hub.MID_LAYER){
			GraphicEntity ball;
			{
				GraphicEntity inner = new GraphicEntity("squares",Hub.MID_LAYER);
				inner.setFrame(6);
				addChild(inner);
				ball = new GraphicEntity("editor_arrows",Hub.MID_LAYER){
					@Override
					public boolean onClick(MotionEvent e){
						if(e.getAction()==MotionEvent.ACTION_UP){
							Gui.removeOnClick(this);
							Gui.removeOnClick(this);
						}
						return true;
					}
					@Override
					public boolean onHover(MotionEvent e){
						if(e.getY()-getY()>1f/(innerFiles.length-3)*0.8f*0.33f){
							scroll(-1);
						}
						else if(e.getY()-getY()<-1f/(innerFiles.length-3)*0.2f*0.33f){
							scroll(1);
						}
						return true;
					}
				};
				ball.setFrame(4);
				addChild(ball);
			}
			@Override
			public boolean onClick(MotionEvent e){
				if(e.getAction()==MotionEvent.ACTION_DOWN&&
						ball.isWithin(e.getX(), e.getY())){
					Gui.giveOnClick(ball);
					return true;
				}
				return false;
			}
			@Override
			public void resize(float w, float h){
				super.resize(w, h);
				getChild(0).resize(0.006f, h-0.006f);
				ball.resize(0.1f,0.1f);
			}
			@Override
			public float offsetX(int index){
				if(index==0)return 0.003f;
				else return -0.052f;
			}
			@Override
			public float offsetY(int index){
				if(index==0)return 0.003f;
				if(innerFiles.length<=4)return getHeight()/2f;
				int i=0;
				for(;i<innerFiles.length-3;++i){
					if(buttons[0].getText().equals(trimExtension(innerFiles[i].getName()))){
						break;
					}
				}
				return getHeight()-((float)i+1)/(innerFiles.length-3)*0.95f*getHeight();
			}
		};
		scrollBar.resize(0.013f, 0.35f);
		scrollBar.reposition(0.895f, 0.33f);
		scrollBar.setFrame(14);
		addChild(scrollBar);

		upButton = new IconMenuButton("editor_arrows",1){
			@Override
			public void performOnRelease(MotionEvent e){
				scroll(-1);
			}
		};
		upButton.resize(0.15f, 0.15f);
		upButton.reposition(0.825f, 0.67f);
		addChild(upButton);
		downButton = new IconMenuButton("editor_arrows",2){
			@Override
			public void performOnRelease(MotionEvent e){
				scroll(1);
			}
		};
		downButton.resize(0.15f, 0.15f);
		downButton.reposition(0.825f, 0.19f);
		addChild(downButton);

		returnButton = new MenuButton("Return"){
			@Override
			public void performOnRelease(MotionEvent e){
				if(e!=served){
					finish();
				}
			}

		};
		returnButton.resize(0.6f, 0.15f);
		returnButton.reposition(0.2f, 0.03f);
		addChild(returnButton);
		checkUpDowns();
	}
	private String trimExtension(String name) {
		if(name.endsWith("."+extension)){
			int indexOfDot = name.lastIndexOf('.');
			return name.substring(0, indexOfDot);
		}
		return name;
	}
	@Override
	public void onMouseScroll(int distance){
		scroll(-distance/120);
	}
	private void scroll(int dy){
		if(innerFiles.length<=4){
			return;
		}
		else {
			if(dy==1){
				for(int i=3;i<innerFiles.length-1;++i){
					if(buttons[3].getText().equals(trimExtension(innerFiles[i].getName()))){
						for(int j=3;j>=0;--j){
							buttons[j].changeText(trimExtension(innerFiles[i+1].getName()));
							--i;
						}
						break;
					}
				}
				if(buttons[3].getText().equals(trimExtension(innerFiles[innerFiles.length-1].getName()))){
					downButton.setVisible(false);
				}
				upButton.setVisible(true);
				if(scrollOffset<innerFiles.length-3){
					++scrollOffset;
				}
			}
			else if(dy==-1){
				for(int i=1;i<innerFiles.length-3;++i){
					if(buttons[0].getText().equals(trimExtension(innerFiles[i].getName()))){
						for(int j=0;j<4;++j){
							buttons[j].changeText(trimExtension(innerFiles[i-1].getName()));
							++i;
						}
						break;
					}
				}
				if(buttons[0].getText().equals(trimExtension(innerFiles[0].getName()))){
					upButton.setVisible(false);
				}
				downButton.setVisible(true);
				if(scrollOffset>0){
					--scrollOffset;
				}
			}
		}
		scrollBar.reposition(scrollBar.getX(), scrollBar.getY());
	}
	private void changeButtons(File folder) {
		this.currentFolder = folder;
		this.innerFiles = folder.listFiles();
		for(int i=0;i<innerFiles.length&&i<4;++i){
			buttons[i].changeText(trimExtension(innerFiles[i].getName()));
			buttons[i].setVisible(true);
		}
		for(int i=innerFiles.length;i<4;++i){
			buttons[i].setVisible(false);
		}
		if(!filePath.isEmpty()){
			backButton.setVisible(true);
		}
		else {
			backButton.setVisible(false);
		}
		if(innerFiles.length>=4){
			returnButton.reposition(0.2f, 0.03f);			
		}
		else {
			returnButton.reposition(0.2f, 0.67f-0.16f*innerFiles.length);
		}
		scrollOffset=0;
		checkUpDowns();
	}
	private void checkUpDowns(){
		scrollBar.setVisible(true);
		downButton.setVisible(true);
		upButton.setVisible(true);
		
		if(innerFiles.length<=4||
			buttons[3].getText().equals(trimExtension(innerFiles[innerFiles.length-1].getName()))){
			downButton.setVisible(false);
		}
		if(innerFiles.length<=4||
		   buttons[0].getText().equals(trimExtension(innerFiles[0].getName()))){
			upButton.setVisible(false);
		}
		if(innerFiles.length<=4){
			scrollBar.setVisible(false);
		}
	}

	private File getFile(){
		return foundFile;
	}
	private void setFile(File file){
		this.foundFile = file;
		synchronized(this){
			this.notifyAll();
		}
	}

	private void finish() {
		this.finished = true;
		synchronized(this){
			this.notifyAll();
		}
	}
	private boolean isFinished() {
		return finished;
	}
	public static File getFile(GraphicView parentView, String startFolder, boolean canStartNew){
		GetFileMenu menu = new GetFileMenu(parentView,startFolder, canStartNew);
		Gui.setView(menu);
		synchronized(parentView){
			parentView.notifyAll();					
		}
		try {
			synchronized(menu){
				while(menu.getFile()==null&&!menu.isFinished()){
					menu.wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Gui.setView(parentView);
		return menu.getFile();
	}
}
