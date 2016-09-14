package game.menu;

import java.io.File;
import java.util.Stack;

import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;

public class GetFileMenu extends Menu{


	private GraphicView parentView;
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

	public GetFileMenu(GraphicView parentView, String startFolder) {
		super();
		this.parentView = parentView;
		this.currentFolder = new File("data"+File.separator+startFolder);
		innerFiles = currentFolder.listFiles();
		for(int i=0;i<4;++i){
			final int fileIndex = i;
			buttons[i] = new MenuButton(innerFiles[i].getName()){
				@Override
				public void performOnRelease(MotionEvent e){
					if(e!=served&&this.isVisible()){
						if(innerFiles[fileIndex].isDirectory()){
							filePath.push(currentFolder);
							changeButtons(innerFiles[fileIndex]);
						}
						else {
							setFile(innerFiles[fileIndex]);
						}
						served = e;
					}
				}

			};
			buttons[i].resize(0.6f, 0.15f);
			buttons[i].reposition(0.2f, 0.67f-0.16f*i);
			addChild(buttons[i]);
		}
		backButton = new IconMenuButton("editor_button",6){
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
		
		scrollBar = new GraphicEntity("squares",1){
			GraphicEntity ball;
			{
				GraphicEntity inner = new GraphicEntity("squares",1);
				inner.setFrame(6);
				addChild(inner);
				ball = new GraphicEntity("editor_button",1){
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
				ball.setFrame(7);
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
					if(buttons[0].getText().equals(innerFiles[i].getName())){
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
		
		upButton = new IconMenuButton("editor_button",4){
			@Override
			public void performOnRelease(MotionEvent e){
				scroll(-1);
			}
		};
		upButton.resize(0.15f, 0.15f);
		upButton.reposition(0.825f, 0.67f);
		addChild(upButton);
		downButton = new IconMenuButton("editor_button",5){
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
					if(buttons[3].getText().equals(innerFiles[i].getName())){
						for(int j=3;j>=0;--j){
							buttons[j].changeText(innerFiles[i+1].getName());
							--i;
						}
						break;
					}
				}
				if(buttons[3].getText().equals(innerFiles[innerFiles.length-1].getName())){
					downButton.setVisible(false);
				}
				upButton.setVisible(true);
			}
			else if(dy==-1){
				for(int i=1;i<innerFiles.length-3;++i){
					if(buttons[0].getText().equals(innerFiles[i].getName())){
						for(int j=0;j<4;++j){
							buttons[j].changeText(innerFiles[i-1].getName());
							++i;
						}
						break;
					}
				}
				if(buttons[0].getText().equals(innerFiles[0].getName())){
					upButton.setVisible(false);
				}
				downButton.setVisible(true);
			}
		}
		scrollBar.reposition(scrollBar.getX(), scrollBar.getY());
	}
	private void changeButtons(File folder) {
		this.currentFolder = folder;
		this.innerFiles = folder.listFiles();
		for(int i=0;i<innerFiles.length&&i<4;++i){
			buttons[i].changeText(innerFiles[i].getName());
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
		checkUpDowns();
	}
	private void checkUpDowns(){
		scrollBar.setVisible(true);
		downButton.setVisible(true);
		upButton.setVisible(true);
		if(buttons[3].getText().equals(innerFiles[innerFiles.length-1].getName())||innerFiles.length<=4){
			downButton.setVisible(false);
		}
		if(buttons[0].getText().equals(innerFiles[0].getName())||innerFiles.length<=4){
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
	public static File getFile(GraphicView parentView, String startFolder){
		GetFileMenu menu = new GetFileMenu(parentView,startFolder);
		Gui.setView(menu);
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
