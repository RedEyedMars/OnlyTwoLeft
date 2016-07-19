package game;

import gui.graphics.GraphicEntity;
import gui.graphics.GraphicView;

public class VisionBubble extends GraphicEntity{

	private GraphicEntity mBack = new GraphicEntity("vision_bubbles",1);
	private GraphicEntity mDown = new GraphicEntity("vision_bubbles",1);
	private GraphicEntity mUp = new GraphicEntity("vision_bubbles",1);
	

	private GraphicEntity yBack = new GraphicEntity("vision_bubbles",1);
	private GraphicEntity yDown = new GraphicEntity("vision_bubbles",1);
	private GraphicEntity yUp = new GraphicEntity("vision_bubbles",1);
	
	private GraphicEntity middle = new GraphicEntity("vision_bubbles",1);
	
	private GraphicEntity mine = new GraphicEntity("vision_bubbles",1);
	private GraphicEntity other = new GraphicEntity("vision_bubbles",1);
	private GraphicView you;
	private GraphicView me;
	public VisionBubble(Hero me, Hero you) {
		super("blank");
		
		mine.adjust(0.4f, 0.4f);
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
		
		mine.setX(mx-0.2f);
		mine.setY(my-0.2f);		
		double mAngle = Math.atan2(yy-my,yx-mx);
		mine.getGraphicElement().rotate((float) (mAngle*mpi));
		
		other.setX(yx-other.getWidth()/2f);
		other.setY(yy-other.getHeight()/2f);
		double yAngle = Math.atan2(my-yy,mx-yx);
		other.getGraphicElement().rotate((float) (yAngle*mpi));
		
		float radius = (float) Math.sqrt(Math.pow(yx-mx, 2)+Math.pow(yy-my, 2));
		if(radius>0.4f){			
			other.setFrame(1);
			mine.setFrame(1);
			if(0.8f-radius>you.getWidth()+0.1f){
				other.adjust(.8f-radius, 0.8f-radius);
			}
			else {
				other.adjust(you.getWidth()+0.1f, you.getHeight()+0.1f);
			}
		}
		else {
			other.adjust(0.4f, 0.4f);
			other.setFrame((int) (6-(radius+0.1f)/0.4f));
			mine.setFrame((int) (6-(radius+0.1f)/0.4f));
		}

		mUp.adjust(0.4f,1f);
		mUp.setX((float) (mine.getX()-(mine.getWidth()+0.3f)*Math.cos(mAngle-Math.PI/2f)));
		mUp.setY((float) (mine.getY()-(mine.getHeight()+0.3f)*Math.sin(mAngle-Math.PI/2f))-0.3f);
		mUp.getGraphicElement().rotate((float) (mAngle*mpi));

		mDown.adjust(0.4f,1f);
		mDown.setX((float) (mine.getX()-(mine.getWidth()+0.3f)*Math.cos(mAngle+Math.PI/2f)));
		mDown.setY((float) (mine.getY()-(mine.getHeight()+0.3f)*Math.sin(mAngle+Math.PI/2f))-0.3f);
		mDown.getGraphicElement().rotate((float) (mAngle*mpi));
		
		mBack.adjust(1f,2f);
		mBack.setX((float) (mine.getX()-(mine.getWidth()+0.3f)*Math.cos(mAngle))-0.3f);
		mBack.setY((float) (mine.getY()-(mine.getHeight()+0.3f)*Math.sin(mAngle))-0.8f);
		mBack.getGraphicElement().rotate((float) (mAngle*mpi));
		
		float ad = 0f;
		if(radius>0.4f){
			ad=radius-0.4f;
			if(ad>0.4f-(me.getWidth()+0.1f)){
				ad=0.4f-(me.getWidth()+0.1f);
			}
		}
		yBack.adjust(1f,2f);
		yBack.setX((float) (other.getX()-(0.4f-ad/2f+0.3f)*Math.cos(yAngle))-ad/2f-0.3f);
		yBack.setY((float) (other.getY()-(0.4f-ad/2f+0.3f)*Math.sin(yAngle))-ad/2f-0.8f);
		yBack.getGraphicElement().rotate((float) (yAngle*mpi));

		yUp.adjust(0.4f,1f);
		yUp.setX((float) (other.getX()-(0.4f-ad/2f+0.3f)*Math.cos(yAngle+Math.PI/2f)-ad/2f));
		yUp.setY((float) (other.getY()-(0.4f-ad/2f+0.3f)*Math.sin(yAngle+Math.PI/2f))-0.3f-ad/2f);
		yUp.getGraphicElement().rotate((float) (mAngle*mpi));

		yDown.adjust(0.4f,1f);
		yDown.setX((float) (other.getX()-(0.4f-ad/2f+0.3f)*Math.cos(yAngle-Math.PI/2f)-ad/2f));
		yDown.setY((float) (other.getY()-(0.4f-ad/2f+0.3f)*Math.sin(yAngle-Math.PI/2f))-0.3f-ad/2f);
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
		middle.adjust(ad,4f);
		middle.setX((float) (mine.getX()-(mine.getWidth()-0.205f+ad/2f)*Math.cos(yAngle))+0.2f-ad/2f);
		middle.setY((float) (mine.getY()-(mine.getHeight()-0.205f+ad/2f)*Math.sin(yAngle))-1.8f);
		middle.getGraphicElement().rotate((float) (mAngle*mpi));
	}

}
