package game.environment.oncreate;

import java.util.List;

public class ForOnCreateAction extends BlockOnCreateAction{

	@Override
	public void act(OnCreateSquare square) {
		for(int i=0;i<ints.get(1);++i){
			for(OnCreateAction action:actions){
				action.act(square);
			}
		}
	}
	@Override
	public void saveArgs(List<Object> saveTo){
		saveTo.add(actions.size());
		saveTo.add(this.ints.get(1));
		for(OnCreateAction action:actions){
			action.saveTo(saveTo);
		}
	}
	@Override
	public int numberOfInts(){
		return 2;
	}
	@Override
	public int getIndex() {
		return 10;
	}

	public OnCreateAction create(){
		return new ForOnCreateAction();
	}
}
