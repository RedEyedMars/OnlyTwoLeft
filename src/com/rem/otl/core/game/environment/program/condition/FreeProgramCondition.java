package com.rem.otl.core.game.environment.program.condition;

import com.rem.otl.core.game.environment.program.ProgrammableSquare;

public class FreeProgramCondition extends ProgramCondition<ProgrammableSquare>{

	@Override
	public boolean satisfies(ProgrammableSquare subject) {
		return true;
	}

	@Override
	public int targetType() {
		return 0;
	}

	@Override
	protected int getIndex() {
		return 0;
	}

	@Override
	public ProgramCondition create() {
		return new FreeProgramCondition();
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
