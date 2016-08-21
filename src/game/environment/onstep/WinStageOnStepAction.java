package game.environment.onstep;

import java.util.List;

import game.Hero;
import game.environment.update.UpdatableSquare;
import main.Hub;

public class WinStageOnStepAction extends OnStepAction<Integer>{
	@Override
	public void act(Hero subject) {
		if(Hub.map.getNextMap(target)!=null){
			System.out.println("Start Next Map:"+Hub.map.getNextMap(target));
			
			subject.getGame().transition(Hub.map.getNextMap(target),true);
		}
		else {
			System.out.println("Map not found:"+target);
		}
	}
	@Override
	public boolean isPassible(){
		return false;
	}
	@Override
	public int getIndex() {
		return 6;
	}
	public int targetType(){
		return 2;
	}
	public void saveTo(List<Object> saveTo){
		saveTo.add(getIndex());
		if(target!=null){
			saveTo.add(target);
		}
		else {
			saveTo.add(-2);
		}
	}
	@Override
	public OnStepAction<Integer> create() {
		return new WinStageOnStepAction();
	}
}
