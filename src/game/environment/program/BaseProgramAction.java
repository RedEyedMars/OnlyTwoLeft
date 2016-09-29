package game.environment.program;

public class BaseProgramAction extends ProgramAction<ProgrammableSquare>{

	@Override
	public void act(ProgrammableSquare subject) {
	}
	@Override
	public int getIndex() {
		return 0;
	}
	@Override
	public int targetType() {
		return 0;
	}

	@Override
	public ProgramAction create() {
		return new BaseProgramAction();
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
