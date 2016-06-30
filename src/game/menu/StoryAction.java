package game.menu;

import game.Action;
import main.Hub;

public abstract class StoryAction implements Action<StoryScene>{

	public abstract boolean  isDone();

	public static StoryAction places(
			final int frontLeft,
			final int backLeft, 
			final int backRight,
			final int frontRight){
		return new StoryAction(){
			@Override
			public void act(StoryScene scene) {
				if(frontLeft!=-1){
					scene.getActor(1).setVisible(true);
					scene.getActor(1).setFrame(frontLeft);
				} else scene.getActor(1).setVisible(false);
				if(backLeft!=-1){
					scene.getActor(0).setVisible(true);
					scene.getActor(0).setFrame(backLeft);
				} else scene.getActor(0).setVisible(false);
				if(frontRight!=-1){
					scene.getActor(3).setVisible(true);
					scene.getActor(3).setFrame(frontRight);
				} else scene.getActor(3).setVisible(false);
				if(backRight!=-1){
					scene.getActor(2).setVisible(true);
					scene.getActor(2).setFrame(backRight);
				} else scene.getActor(2).setVisible(false);
			}
			@Override
			public boolean isDone() {
				return true;
			}

		};	
	}

	public static StoryAction text(final String ftext){
		return new StoryAction(){
			private int index = 1;
			private String text = ftext+"            ";
			@Override
			public void act(StoryScene scene) {
				scene.getText().setValue(text.substring(0,index));
				++index;
			}
			@Override
			public boolean isDone() {
				return index>=text.length();
			}

		};	
	}

	public static StoryAction next(final int i) {
		return new StoryAction(){

			@Override
			public void act(StoryScene scene) {
				Hub.sceneIndex = i;
				Hub.scenes.get(Hub.sceneIndex).act(null);
			}

			@Override
			public boolean isDone() {
				return true;
			}};
	}
	
	
}
