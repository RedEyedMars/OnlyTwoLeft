package editor.program;

import editor.Button;
import editor.ButtonAction;
import game.environment.program.condition.ProgramCondition;

public class ConditionArrow extends Button{
	private ProgramCondition condition;
	private String event;

	public ConditionArrow(ProgramCondition condition, String event) {
		super("editor_button", 3, null);
		this.condition = condition;
		this.event = event;
		setAction(new ButtonAction(){
			@Override
			public void act(Object subject) {
				
			}});
	}

	public ProgramCondition getCondition() {
		return condition;
	}

	public String getEvent() {
		return event;
	}
}
