package game.environment.program.action;

import game.environment.update.NullUpdateAction;
import game.environment.update.UpdateAction;

public class SetUpdateActionProgramAction extends ProgramAction<UpdateAction>{

	public SetUpdateActionProgramAction(){
		super();
		setData("subject", new NullUpdateAction());
	}
	@Override
	public int targetType() {
		return 10;
	}

	@Override
	public int getIndex() {
		return 2;
	}

	@Override
	public void act(UpdateAction subject) {
		this.getState().getTarget().setUpdateAction(subject);
	}

	@Override
	public ProgramAction create() {
		return new SetUpdateActionProgramAction();
	}
	@Override
	public String[] copiableIntTextureNames() {
		return new String[]{};
	}
	@Override
	public int[] copiableIntTextureRanges(){
		return new int[]{};
	}

}
