package game.environment.update;

public class GrowUpdateAction extends UpdateAction{
	private float growthW = 0f;
	private float growthH = 0f;
	public GrowUpdateAction(){
		defaultState = false;
	}
	@Override
	public void act(Double seconds) {
		growthW += x*seconds;
		growthH += y*seconds;
		self.adjust((float) (self.getWidth()+x*seconds), (float) (self.getHeight()+y*seconds));
		if(onLimitBrokenAction>-1&&Math.sqrt(growthW*growthW+growthH*growthH)>=limit){
			limiters.get(onLimitBrokenAction).act(this);
			growthW=0f;
			growthH=0f;
		}
	}
	@Override
	public void flip(){
		growthH=-growthH;
		y=-y;
	}
	@Override
	public void undo(){
		self.adjust(self.getWidth()-growthW, self.getHeight()-growthH);
	}
	@Override
	public int getIndex() {
		return 0;
	}
	@Override
	public UpdateAction create(){
		return new GrowUpdateAction();
	}
}
