package game.environment.update;

public class GrowUpdateAction extends UpdateAction{
	private float growthW = 0f;
	private float growthH = 0f;
	public GrowUpdateAction(){
		defaultState = false;
	}
	@Override
	public void act(Double seconds) {
		float dx = (float) (x*seconds);
		float dy = (float) (y*seconds);
		growthW += dx;
		growthH += dy;
		limiter+=Math.sqrt(dx*dx+dy*dy);
		if(onLimitBrokenAction>-1&&limiter>=limit){
			if(dx==0){
				self.adjust(self.getWidth(),self.getHeight()+dy-Math.signum(y)*(limiter-limit));
			}
			else if(dy==0){
				self.adjust(self.getWidth()+dx-Math.signum(x)*(limiter-limit),self.getHeight());
			}
			limiters.get(onLimitBrokenAction).act(this);
			growthW=0f;
			growthH=0f;
			limiter=0f;
		}
		else {
			self.adjust(self.getWidth()+dx,self.getHeight()+dy);
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
