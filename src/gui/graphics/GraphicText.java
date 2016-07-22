package gui.graphics;

import java.util.ArrayList;
import java.util.List;

import main.Hub;

public class GraphicText extends GraphicEntity {

	private float visualW=1f;
	private float visualH=1f;
	
	protected String text;
	protected List<GraphicLine> lines = new ArrayList<GraphicLine>();

	protected int charIndex=0;
	protected int lineIndex = 0;
	private GraphicText self = this;
	private String font;
	private int layer;
	protected GraphicEntity blinker = new GraphicEntity("squares",1){
		private double since;
		private int horz = charIndex;
		private int vert = lineIndex;
		@Override
		public void update(double time){
			since+=time;
			if(isVisible()&&since>1){
				blinker.setVisible(!blinker.isVisible());
				since-=1f;
			}
			else if(!isVisible()&&since>0.5f){
				blinker.setVisible(!blinker.isVisible());
				since-=0.5f;
			}
			if(blinker.isVisible()){
				if(vert!=lineIndex||horz!=charIndex){
					setY(self.getY()-lineIndex*0.025f);
					vert = lineIndex;
					if(vert<lines.size()&&lines.get(vert).length()>0&&charIndex>0){
						if(charIndex>=lines.get(vert).length()){
							setX(self.getX()+
									lines.get(vert).chars.get(lines.get(vert).length()-1).getX()+
									lines.get(vert).chars.get(lines.get(vert).length()-1).getWidth()/2f);
						}
						else {
							setX(self.getX()+
									lines.get(vert).chars.get(charIndex-1).getX()+
									lines.get(vert).chars.get(charIndex-1).getWidth()/2f);
						}
					}
					else {
						setX(self.getX());
					}
					horz=charIndex;
				}
			}
		}
	};
	public GraphicText(String font, String text, int layer) {
		super("blank");
		this.font = font;
		this.text = text;
		this.layer = layer;
		String[] lines = text.split("\n");
		for(int i=0;i<lines.length;++i){
			GraphicLine line = new GraphicLine(lines[i]);
			this.lines.add(line);
			addChild(line);
		}
		this.adjust(1f, 1f);
		this.setX(0f);
		this.setY(0.97f);
		blinker.setFrame(7);
		blinker.adjust(0.005f,0.025f);
		blinker.setY(0.975f);
		blinker.setVisible(false);

	}

	public String getText(){
		return text;
	}

	public void change(String text){
		this.onRemoveFromDrawable();
		this.text = text;
		String[] lines = text.split("\n");
		int size = this.lines.size();
		if(size<lines.length){
			for(int i=size;i<lines.length;++i){
				GraphicLine line = new GraphicLine(lines[i]);
				this.lines.add(line);
			}
		}
		for(int i=0;i<size;++i){
			if(i<lines.length){
				this.lines.get(i).change(lines[i]);
			}
			else {
				this.lines.get(i).change("");
			}
		}
		for(GraphicLine line:this.lines){
			addChild(line);
		}

		this.adjust(getWidth(), getHeight());
		this.setX(getX());
		this.setY(getY());
		addChild(blinker);
		this.onAddToDrawable();
	}

	@Override
	public float offsetY(int index){
		return 0.025f*(-index)*visualH;
	}

	public void setWidthFactor(float w){
		this.visualW = w;
	}
	public void setHeightFactor(float h){
		this.visualH = h;
	}

	protected class GraphicLine extends GraphicEntity{
		private String text;
		private float offset = 0f;
		private List<GraphicChar> chars = new ArrayList<GraphicChar>();
		private int length;
		public GraphicLine(String text) {
			super("blank");
			this.text = text;
			this.length = this.text.length();
			char[] chars = text.toCharArray();
			for(int i=0;i<chars.length;++i){
				GraphicChar c = new GraphicChar(chars[i]);
				this.chars.add(c);
				addChild(new GraphicChar(chars[i]));
			}
		}
		public void change(String string) {
			this.text = string;
			this.length = this.text.length();
			int size = chars.size();
			if(size<string.length()){
				for(int i=size;i<string.length();++i){
					GraphicChar c = new GraphicChar(string.charAt(i));
					this.chars.add(c);					
				}
			}
			for(int i=0;i<size;++i){
				if(i<string.length()){
					this.chars.get(i).change(string.charAt(i));
				}
				else {
					this.chars.get(i).change(' ');
				}
			}
			for(GraphicChar c:this.chars){
				addChild(c);
			}
		}
		@Override
		public float offsetX(int index){
			if(index==0){
				offset = 0;
			}
			else if(index<chars.size()){
				offset+=getChild(index-1).getWidth()*chars.get(index-1).getWidthValue()*visualW;
			}

			return offset;
		}
		public int length() {
			return length;
		}
		public String getText() {
			return text;
		}

	}
	private class GraphicChar extends GraphicEntity{
		private float value;

		public GraphicChar(char c) {
			super("$"+font,layer);
			setFrame(c);
			value = Hub.renderer.letterWidths.get(font).get(c)*14/16;
		}

		public float getWidthValue() {
			return this.value;
		}

		public void change(char c) {
			setFrame(c);
			value = Hub.renderer.letterWidths.get(font).get(c)*14/16;
		}

		@Override
		public void adjust(float x, float y){
			super.adjust(0.025f*visualW,0.025f*visualH);
		}
	}
}
