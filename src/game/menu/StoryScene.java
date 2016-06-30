package game.menu;

import java.util.ArrayList;
import java.util.List;

import game.Action;
import game.environment.Square;
import gui.Gui;
import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;
import gui.inputs.MotionEvent;
import main.Hub;

public class StoryScene extends GraphicView{
	private GraphicWord text;
	private List<GraphicEntity> actors = new ArrayList<GraphicEntity>();
	private StoryAction[] actions;
	private int storyIndex = 0;
	private int skip = 2;
	public StoryScene(StoryAction... actions){
		super();
		addChild(new Square(Square.blue,1f));

		GraphicEntity actor = new GraphicEntity("characters");
		actors.add(actor);
		actor.adjust(0.4f, 0.4f);
		actor.setX(0.08f);
		actor.setY(0.48f);
		actor = new GraphicEntity("characters");
		actors.add(actor);
		actor.adjust(0.4f, 0.4f);
		actor.setY(0.28f);
		actor = new GraphicEntity("characters");
		actors.add(actor);
		actor.adjust(0.4f, 0.4f);
		actor.setX(0.54f);
		actor.setY(0.49f);
		actor = new GraphicEntity("characters");
		actors.add(actor);
		actor.adjust(0.4f, 0.4f);
		actor.setX(0.62f);
		actor.setY(0.29f);
		for(GraphicEntity a:actors){
			a.setVisible(false);
			addChild(a);
		}


		addChild(new GraphicEntity("speech_bubble"){{
			adjust(0.16f,0.3f);
			setX(0.03f);
			setY(0.025f);
		}
		});		

		addChild(new GraphicEntity("speech_bubble"){{
			adjust(0.68f,0.3f);
			setX(0.16f);
			setY(0.025f);
			setFrame(1);
		}
		});
		addChild(new GraphicEntity("speech_bubble"){{
			adjust(0.16f,0.3f);
			setX(0.81f);
			setY(0.025f);
			setFrame(2);
		}
		});
		text = new GraphicWord(26*7,true);
		text.setX(0.08f);
		text.setY(0.24f);
		addChild(text);

		this.actions = actions;
	}

	private int tick = 0;
	@Override
	public void update(double secondsSinceLastFrame){
		if(tick%5==0){
			actions[storyIndex].act(this);
			if(actions[storyIndex].isDone()||skip<0){
				skip = 1;
				++storyIndex;
			}
		}
		++tick;
		super.update(secondsSinceLastFrame);
	}

	public GraphicEntity getActor(int i) {
		return actors.get(i);		
	}

	public GraphicWord getText() {
		return text;
	}

	@Override
	public boolean onClick(MotionEvent e){
		if(e.getAction()==MotionEvent.ACTION_UP){
			--skip;
		}
		return true;
	}

	public static void setupScenes(){
		//0
		Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new StoryScene(		
				StoryAction.places(0, -1, -1, 2),
				StoryAction.text("Scrambo\n\n"
						+ "pinki have I ever told\n"
						+ "you how much I enjoy\nhanging out with you"),
				StoryAction.text("pinki\n\n"
						+ "Oh scrambo i enjoy your\n"
						+ "company as well"),
				StoryAction.text("Scrambo\n\n"
						+ "For me its more\nthan that"),
				StoryAction.text("Scrambo\n\n"
						+ "i              "),
				StoryAction.places(0, 1, -1, 3),
				StoryAction.text("Redcardo\n\n"
						+ "pinki there you are\nLets go"),
				StoryAction.places(0, -1, 2, 3),
				StoryAction.text("Redcardo\n\n"
						+ "see you later\nscrambo"),
				StoryAction.places(0, -1, -1, -1),
				StoryAction.text("Scrambo\n\n"
						+ "Hes kidnapped her      \nI have to save pinki\nfrom Redcardos evil clutches"),
				StoryAction.next(1)));}});
		//1
		/*Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new WinnableEndlessMode(300){			
			@Override
			public void endGame(){
				Hub.sceneIndex=0;
				super.endGame();
			}
			@Override
			public void winGame(){
				StoryAction.next(2).act(null);
			}
		});}});*/
		//2
		Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new StoryScene(		
				StoryAction.places(0, -1, 4, -1),
				StoryAction.text("Scrambo\n\n"
						+ "Maroonbo have you seen pinki"),
				StoryAction.places(0, -1, -1, 4),
				StoryAction.text("Maroonbo\n\n"
						+ "Yeah scrambo but"),
				StoryAction.text("Scrambo\n\n"
						+ "what"),
				StoryAction.text("Maroonbo\n\n"
						+ "maybe you should give\nher time alone with Rickie"),
				StoryAction.text("Scrambo\n\n"
						+ "Shes been taken by that\nevil redcardo\ni must save her"),
				StoryAction.next(3)));}});
		//3
		/*Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new BattleMode(){
			{
				enemy.setVelocity(-0.2f, hero);
				enemy.adjust(enemy.getWidth()*2, enemy.getHeight());
				enemyBulletFrequency = 3;
				enemyBulletSpeed = 0.0075f;
				bulletFrequency = 5;
				bulletSpeed = 0.005f;
			}
			@Override
			public void update(){
				super.update();
				enemyPointerX = enemy.getX()+(hero.getX()-enemy.getX()+0.0375f)/2f;
				enemy.setY(1f-enemy.getHeight());
			}
			@Override
			public void endGame(){
				Hub.sceneIndex=2;
				super.endGame();
			}
			@Override
			public void winGame(){
				Gui.removeOnType(this);
				Hub.addLayer.clear();
				StoryAction.next(4).act(null);
			}
		});}});*/
		//4
		Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new StoryScene(
				StoryAction.places(0, -1, -1, 4),
				StoryAction.text("Maroonbo\n\n"
						+ "What happened to you\nscrambo   \n"
						+ "you used to be cool"),
				StoryAction.text("Scrambo\n\n"
						+ "Im still cool   \nNow tell me where Redcardo\ntook pinki"),
				StoryAction.text("Maroonbo\n\n"
						+ "Try MacCrims\nRedcardo likes his\nmilkshakes"),
				StoryAction.next(5)));}});
		//5
		/*Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new WinnableEndlessMode(300){
			private float borderX=0.5f;
			private float dirX=0.015f;
			{				
				for(float y = 0f;y<=1.0375f;y+=0.0375f){
					FunctionalSquare left = new FunctionalSquare("black",1f);
					left.setSide("red");
					addSquare(left);
					borders.add(left);
					FunctionalSquare right = new FunctionalSquare("black",1f);
					right.setSide("red");
					addSquare(right);
					borders.add(right);
					generateSideRail(y,left,right);
					incrementBorderX();
				}
				FunctionalSquare regenerator = new FunctionalSquare("black",1.0f){
					private int index=0;
					@Override
					public Action<FunctionalSquare> getOnHitAction(FunctionalSquare hitter){
						return new Action<FunctionalSquare>(){
							@Override
							public void act(FunctionalSquare subject) {
								if(subject.getX()<0.2f){
									generateSideRail(1.0375f+subject.getY(),borders.get(3+index),borders.get(4+index));
									index+=2;
									if(index+4>=borders.size())index=0;
									incrementBorderX();
								}
							}
						};
					}
				};
				regenerator.move(-0.2f,-1.0375f);
				addSquare(regenerator);
			}
			private void generateSideRail(float y, FunctionalSquare left, FunctionalSquare right){
				if(left!=null){
					left.adjust(borderX-0.2f,0.0376f);
					left.setY(y);
					FunctionalSquare botLeft = new FunctionalSquare("black",0f);
					botLeft.move(left.getWidth()/2f,-0.2f);
					left.setVelocity(-0.006f, botLeft);
				}
				if(right!=null){
					right.adjust(1f-borderX+0.2f,0.0376f);
					right.move(borderX+0.2f,y);
					FunctionalSquare botRight = new FunctionalSquare("black",0f);
					botRight.move(0.2f+borderX+right.getWidth()/2f,-0.2f);
					right.setVelocity(-0.006f, botRight);
				}
			}
			private void incrementBorderX(){
				borderX+=dirX;
				if(borderX<0.2f){
					borderX=0.2f;
					dirX= (-1)*dirX;
				}
				else if(borderX>0.8){
					borderX=0.8f;
					dirX= (-1)*dirX;
				}
				else if(Math.random()<0.2f) {
					dirX= (-1)*dirX;
				}
			}
			@Override
			public void winGame(){
				StoryAction.next(6).act(null);
			}
			@Override
			public void endGame(){
				Hub.sceneIndex=4;
				super.endGame();
			}
			@Override
			public int maxReds(){
				return super.maxReds()+borders.size()-3;
			}
		});}});*/
		//6
		Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new StoryScene(
				StoryAction.places(0, -1, -1, 4),
				StoryAction.text("Maccrim\n\n"
						+ "Howdy stranger \ncare for a milkshake"),
				StoryAction.text("Scrambo\n\n"
						+ "no milkshakes\nhave you seen a pink square\nshe would probably\nbe in distress"),
				StoryAction.text("Maccrim\n\n"
						+ "We had a pink lass in\nhere not too long ago\nbut she and her man seemed\nin good spirits"),
				StoryAction.text("Scrambo\n\n"
						+"fine fine but where did they go"),
				StoryAction.text("Maccrim\n\n"
						+ "beats me laddy"),
				StoryAction.text("Scrambo\n\n"
						+"liar\nI will make you tell me\nI have to save pinki"),
				StoryAction.next(7)));}});
		//7
		/*Hub.scenes.add(new Action(){public void act(Object o){Gui.setView(new BattleMode(){
			private FunctionalSquare leftTop = new FunctionalSquare("black",0.05f);
			private List<FunctionalSquare> walls = new ArrayList<FunctionalSquare>();
			private float dirX = 0.005f;
			{
				this.enemyBulletFrequency=15;
				this.bulletFrequency=5;
				leftTop.move(-0.05f, 1f);
				for(int i=0;i<20;++i){
					makeWall(i);
				}
				enemy.setVelocity(dirX, leftTop);
			}
			private void makeWall(int i){
				final FunctionalSquare destination = new FunctionalSquare("black",0.05f);
				destination.setX(i*0.05f);
				FunctionalSquare wall = new FunctionalSquare("red",0.05f){
					@Override
					public Action<FunctionalSquare> getOnHitAction(FunctionalSquare hitter){
						final FunctionalSquare self = this;
						return new Action<FunctionalSquare>(){
							@Override
							public void act(FunctionalSquare subject) {
								removeSquare(subject);
								if(self.getSpeed()<=0.0025f){
									self.increaseSpeed(0.002f);									
								}
							}							
						};
					}
				};
				wall.setVelocity(-0.0015f,hero);
				wall.move(i*0.05f, (float)Math.sin(Math.PI*i*0.05f)*0.5f);
				walls.add(wall);
				addSquare(wall);
				borders.add(wall);
			}
			@Override
			public void update(){
				if(enemy.getX()<0.01){
					dirX=0.005f;
					enemy.setVelocity(dirX, leftTop);	
				}
				else if(enemy.getX()>0.99){
					dirX=-0.005f;
					enemy.setVelocity(dirX, leftTop);	
				}
				if(enemy.getX()<0f){
					enemy.setX(0.025f);
				}
				walls.get((int) (19*(enemy.getX()))).setVelocity(-0.0015f,hero);
				enemyPointerX = enemy.getX();
				super.update();
			}
			@Override
			public void endGame(){
				Hub.sceneIndex=6;
				super.endGame();
			}
			@Override
			public void winGame(){
				Gui.removeOnType(this);
				Hub.addLayer.clear();
				StoryAction.next(0).act(null);
			}
		});}});*/
	}


}
