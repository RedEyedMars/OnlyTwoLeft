package com.rem.otl.core.game.menu;

import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.game.Action;
import com.rem.otl.core.game.environment.Square;
import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

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
		actor.resize(0.4f, 0.4f);
		actor.reposition(0.08f,0.48f);
		actor = new GraphicEntity("characters");
		actors.add(actor);
		actor.resize(0.4f, 0.4f);
		actor.reposition(actor.getX(),0.28f);
		actor = new GraphicEntity("characters");
		actors.add(actor);
		actor.resize(0.4f, 0.4f);
		actor.reposition(0.54f,0.49f);
		actor = new GraphicEntity("characters");
		actors.add(actor);
		actor.resize(0.4f, 0.4f);
		actor.reposition(0.62f,0.29f);
		for(GraphicEntity a:actors){
			a.setVisible(false);
			addChild(a);
		}


		addChild(new GraphicEntity("speech_bubble"){{
			resize(0.16f,0.3f);
			reposition(0.03f,0.025f);
		}
		});		

		addChild(new GraphicEntity("speech_bubble"){{
			resize(0.68f,0.3f);
			reposition(0.16f,0.025f);
			setFrame(1);
		}
		});
		addChild(new GraphicEntity("speech_bubble"){{
			resize(0.16f,0.3f);
			reposition(0.81f,0.025f);
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
	public boolean onClick(ClickEvent e){
		if(e.getAction()==ClickEvent.ACTION_UP){
			--skip;
		}
		return true;
	}

	public static void setupScenes(){
		
	}


}
