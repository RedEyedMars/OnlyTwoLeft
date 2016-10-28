package com.rem.otl.core.duo.messages;

import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.duo.Handler;
import com.rem.otl.core.game.Action;

public class ActionMessage extends Message{
	private static final long serialVersionUID = -6581824971280174492L;
	private static List<Action<Object>> actions = new ArrayList<Action<Object>>();
	private int indexOfAction;
	public ActionMessage(Action<Object> action){		
		indexOfAction = actions.size();
		actions.add(action);
	}

	@Override
	public void act(Handler handler) {
		if(indexOfAction<actions.size()){
			actions.remove(indexOfAction).act(null);		
		}
		else {
			actions.remove(actions.size()-1).act(null);
		}
	}

}
