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
	private List<GraphicEntity> actors = new ArrayList<GraphicEntity>();
	private StoryAction[] actions;
	private int storyIndex = 0;
	private int skip = 2;
	public StoryScene(StoryAction... actions){
		super();
		//addChild(new Square(Square.blue,1f));

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

	@Override
	public boolean onClick(MotionEvent e){
		if(e.getAction()==MotionEvent.ACTION_UP){
			--skip;
		}
		return true;
	}

	public static void setupScenes(){
		
	}


}
