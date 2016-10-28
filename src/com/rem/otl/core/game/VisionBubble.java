package com.rem.otl.core.game;

import com.rem.otl.core.game.hero.Hero;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.main.Hub;

public class VisionBubble extends GraphicEntity{

	private GraphicEntity mBack = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	private GraphicEntity mDown = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	private GraphicEntity mUp = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	

	private GraphicEntity yBack = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	private GraphicEntity yDown = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	private GraphicEntity yUp = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	
	private GraphicEntity middle = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	
	private GraphicEntity mine = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	private GraphicEntity other = new GraphicEntity("vision_bubbles",Hub.MID_LAYER);
	private GraphicEntity you;
	private GraphicEntity me;
	public VisionBubble(Hero me, Hero you) {
		super("blank");
		
		mine.resize(0.4f, 0.4f);
		addChild(mine);		
		addChild(other);
		
		addChild(mBack);	
		addChild(mUp);	
		addChild(mDown);	
		
		addChild(yBack);	
		addChild(yUp);	
		addChild(yDown);	

		addChild(middle);	
		
		this.me = me;
		this.you = you;
	}
	
	private double mpi = 360/Math.PI/2f;
	public void update(double update){

		float mx = me.getX()+me.getWidth()/2f;
		float my = me.getY()+me.getHeight()/2f;
		float yx = you.getX()+you.getWidth()/2f;
		float yy = you.getY()+you.getHeight()/2f;
		
		double yAngle = 0f;
		double mAngle = Math.PI;
		if(yy-my!=0||yx-mx!=0){
			mAngle = Math.atan2(yy-my,yx-mx);
			yAngle = Math.atan2(my-yy,mx-yx);
		}
		
		
		mine.reposition(mx-0.2f,
                    my-0.2f);
		mine.getGraphicElement().rotate((float) (mAngle*mpi));
		
		other.reposition(yx-other.getWidth()/2f,
				     yy-other.getHeight()/2f);
		other.getGraphicElement().rotate((float) (yAngle*mpi));
		
		float radius = (float) Math.sqrt(Math.pow(yx-mx, 2)+Math.pow(yy-my, 2));
		if(radius>0.4f){			
			other.setFrame(1);
			mine.setFrame(1);
			if(0.8f-radius>you.getWidth()+0.1f){
				other.resize(.8f-radius, 0.8f-radius);
			}
			else {
				other.resize(you.getWidth()+0.1f, you.getHeight()+0.1f);
			}
		}
		else {
			other.resize(0.4f, 0.4f);
			float r = radius;
			if(r-0.1<0.4/4){
				other.setFrame(6);
				mine.setFrame(6);
			}
			else if(r-0.15f<0.4/4){
				other.setFrame(5);
				mine.setFrame(5);
			}
			else if(r-0.25f<0.4/4){
				other.setFrame(4);
				mine.setFrame(4);
			}
			else if(r-0.275f<0.4/4){
				other.setFrame(3);
				mine.setFrame(3);
			}
			else {				
					other.setFrame(2);
					mine.setFrame(2);
			}
		}

		mUp.resize(0.4f,1f);
		mUp.reposition((float) (mine.getX()-(mine.getWidth()+0.3f)*Math.cos(mAngle-Math.PI/2f)),
		           (float) (mine.getY()-(mine.getHeight()+0.3f)*Math.sin(mAngle-Math.PI/2f))-0.3f);
		mUp.getGraphicElement().rotate((float) (mAngle*mpi));

		mDown.resize(0.4f,1f);
		mDown.reposition((float) (mine.getX()-(mine.getWidth()+0.3f)*Math.cos(mAngle+Math.PI/2f)),
				     (float) (mine.getY()-(mine.getHeight()+0.3f)*Math.sin(mAngle+Math.PI/2f))-0.3f);
		mDown.getGraphicElement().rotate((float) (mAngle*mpi));
		
		mBack.resize(1f,2f);
		mBack.reposition((float) (mine.getX()-(mine.getWidth()+0.3f)*Math.cos(mAngle))-0.3f,
				     (float) (mine.getY()-(mine.getHeight()+0.3f)*Math.sin(mAngle))-0.8f);
		mBack.getGraphicElement().rotate((float) (mAngle*mpi));
		
		float ad = 0f;
		if(radius>0.4f){
			ad=radius-0.4f;
			if(ad>0.4f-(me.getWidth()+0.1f)){
				ad=0.4f-(me.getWidth()+0.1f);
			}
		}
		yBack.resize(1f,2f);
		yBack.reposition((float) (other.getX()-(0.4f-ad/2f+0.3f)*Math.cos(yAngle))-ad/2f-0.3f,
				     (float) (other.getY()-(0.4f-ad/2f+0.3f)*Math.sin(yAngle))-ad/2f-0.8f);
		yBack.getGraphicElement().rotate((float) (yAngle*mpi));

		yUp.resize(0.4f,1f);
		yUp.reposition((float) (other.getX()-(0.4f-ad/2f+0.3f)*Math.cos(yAngle+Math.PI/2f)-ad/2f),
				   (float) (other.getY()-(0.4f-ad/2f+0.3f)*Math.sin(yAngle+Math.PI/2f))-0.3f-ad/2f);
		yUp.getGraphicElement().rotate((float) (mAngle*mpi));

		yDown.resize(0.4f,1f);
		yDown.reposition((float) (other.getX()-(0.4f-ad/2f+0.3f)*Math.cos(yAngle-Math.PI/2f)-ad/2f),
				   (float) (other.getY()-(0.4f-ad/2f+0.3f)*Math.sin(yAngle-Math.PI/2f))-0.3f-ad/2f);
		yDown.getGraphicElement().rotate((float) (mAngle*mpi));

		ad = 0f;
		if(radius>0.4f){
			if((radius-0.4f)>0.4f-(you.getWidth()+0.1f)){
				ad=(radius-0.4f)+(you.getWidth()/2f+0.1f)+0.01f;
			}
			else {
				ad=(radius-0.4f)*3/2f+0.01f;
			}
		}
		middle.resize(ad,4f);
		middle.reposition((float) (mine.getX()-(mine.getWidth()-0.205f+ad/2f)*Math.cos(yAngle))+0.2f-ad/2f,
				    (float) (mine.getY()-(mine.getHeight()-0.205f+ad/2f)*Math.sin(yAngle))-1.8f);
		middle.getGraphicElement().rotate((float) (mAngle*mpi));
	}
	
	public void setHeroes(GraphicEntity focused, GraphicEntity wild){
		me = focused;
		you = wild;
	}

}
