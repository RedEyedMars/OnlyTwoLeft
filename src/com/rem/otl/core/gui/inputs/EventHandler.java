package com.rem.otl.core.gui.inputs;

import java.util.LinkedList;
import java.util.Stack;

import com.rem.otl.core.gui.inputs.InputEvent;
import com.rem.otl.core.gui.inputs.KeyBoardEvent;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.main.Hub;

public class EventHandler extends Thread {
	public static boolean debug = false;
	public static boolean continuousKeyboard = false;
	private Boolean running = true;
	private LinkedList<ClickEvent> onClickQueue = new LinkedList<ClickEvent>();
	private LinkedList<KeyBoardEvent> onTypeQueue = new LinkedList<KeyBoardEvent>();
	private LinkedList<WheelEvent> onWheelQueue = new LinkedList<WheelEvent>();
	private LinkedList<HoverEvent> onHoverQueue = new LinkedList<HoverEvent>();
	private Stack<KeyBoardListener> keyboardListener = new Stack<KeyBoardListener>();
	private Stack<MouseListener> mouseListener = new Stack<MouseListener>();
	private boolean hasEventToProcess = false;



	public EventHandler(){
		super();
		start();
	}

	@Override
	public void run(){
		try{
			while(running){
				synchronized(this){
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(EventHandler.debug)Hub.log.debug("EventHandler", ">>"+onClickQueue.size());
				while(running&&!onClickQueue.isEmpty()){
					mouseListener.peek().onClick(onClickQueue.removeFirst());
					if(EventHandler.debug)Hub.log.debug("EventHandler", onClickQueue.size());

				}
				if(EventHandler.debug)Hub.log.debug("EventHandler", "release");
				while(running&&!onHoverQueue.isEmpty()){
					mouseListener.peek().onHover(onHoverQueue.removeFirst());

				}
				while(running&&!onWheelQueue.isEmpty()){
					mouseListener.peek().onMouseScroll(onWheelQueue.removeFirst().getAmount());

				}
				while(running&&!onTypeQueue.isEmpty()){
					keyboardListener.peek().onType(onTypeQueue.removeFirst());
				}
				hasEventToProcess =false;
				synchronized(this){
					this.notifyAll();
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			synchronized(this){
				this.notifyAll();					
			}
			Hub.gui.setFinished(true);
		}
	}

	public void handleEvent(InputEvent event){
		if(event.getType()==InputEvent.CLICK){
			onClickQueue.add((ClickEvent) event);
		}
		else if(event.getType()==InputEvent.KEYBOARD){
			onTypeQueue.add((KeyBoardEvent) event);
		}
		else if(event.getType()==InputEvent.WHEEL){
			onWheelQueue.add((WheelEvent) event);
		}
		else if(event.getType()==InputEvent.HOVER){
			onHoverQueue.add((HoverEvent) event);
		}
		hasEventToProcess=true;
	}
	public void end() {
		clear();
		running = false;
		synchronized(this){
			this.notifyAll();
		}
	}

	public void processEvents() {
		if(EventHandler.debug)Hub.log.debug("EventHandler", "processEvents");
		if(hasEventToProcess&&running){
			synchronized(this){
				this.notifyAll();
			}
			synchronized(this){
				try {
					if(hasEventToProcess){
						this.wait();
						if(EventHandler.debug)Hub.log.debug("EventHandler", "RELEASED");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}


	public void giveOnClick(MouseListener listener) {
		mouseListener.push(listener);
		//System.out.println("push"+mouseListener.size()+listener.getClass());
	}

	public void removeOnClick(MouseListener listener) {
		while(!mouseListener.isEmpty()&&mouseListener.pop()!=listener){		
		}
		//System.out.println("pop"+mouseListener.size()+mouseListener.peek().getClass());
	}


	public void giveOnType(KeyBoardListener listener) {
		keyboardListener.push(listener);

		continuousKeyboard = listener.continuousKeyboard();

	}
	public void removeOnType(KeyBoardListener listener) {
		if(!keyboardListener.empty()){
			if(keyboardListener.peek()==listener){
				keyboardListener.pop();
			}
		}

		if(!keyboardListener.empty()){
			continuousKeyboard = keyboardListener.peek().continuousKeyboard();
		}
		//System.out.println("pop"+mouseListener.size()+mouseListener.peek().getClass());
	}
	public void clear(){
		this.keyboardListener.clear();
		this.mouseListener.clear();
	}
}
