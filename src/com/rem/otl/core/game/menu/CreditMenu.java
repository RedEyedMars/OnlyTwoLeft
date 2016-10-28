package com.rem.otl.core.game.menu;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.rem.otl.core.gui.Gui;
import com.rem.otl.core.gui.graphics.GraphicEntity;
import com.rem.otl.core.gui.graphics.GraphicText;
import com.rem.otl.core.gui.graphics.GraphicView;
import com.rem.otl.core.gui.inputs.ClickEvent;
import com.rem.otl.core.gui.music.ITrack;
import com.rem.otl.core.gui.music.Library;
import com.rem.otl.core.main.Hub;

public class CreditMenu extends Menu{
	private List<GraphicEntity> creditScroll = new ArrayList<GraphicEntity>();
	private float scroll = 0.5f;
	private GraphicView view;
	public CreditMenu(final GraphicView xyz){
		super();
		
		
		GraphicText text = new GraphicText("impact","RedEyedMars Presents",Hub.MID_LAYER);
		text.setFontSize(GraphicText.FONT_SIZE_LARGE);
		text.setJustified(GraphicText.MIDDLE_JUSTIFIED);
		creditScroll.add(text);
		CreditButton creditButton = new CreditButton("Older Than Light","https://github.com/RedEyedMars/OnlyTwoLeft");
		creditScroll.add(creditButton);


		creditScroll.add(new GraphicEntity("blank",Hub.BOT_LAYER));
		text = new GraphicText("impact","Art & Design",Hub.MID_LAYER);
		text.setFontSize(GraphicText.FONT_SIZE_LARGE);
		text.setJustified(GraphicText.MIDDLE_JUSTIFIED);
		creditScroll.add(text);
		creditButton = new CreditButton("RedEyedMars","https://github.com/RedEyedMars/OnlyTwoLeft");
		creditScroll.add(creditButton);

		creditScroll.add(new GraphicEntity("blank",Hub.BOT_LAYER));
		text = new GraphicText("impact","Code",Hub.MID_LAYER);
		text.setFontSize(GraphicText.FONT_SIZE_LARGE);
		text.setJustified(GraphicText.MIDDLE_JUSTIFIED);
		creditScroll.add(text);
		creditButton = new CreditButton("RedEyedMars","https://github.com/RedEyedMars/OnlyTwoLeft");
		creditScroll.add(creditButton);

		creditScroll.add(new GraphicEntity("blank",Hub.BOT_LAYER));
		text = new GraphicText("impact","Music",Hub.MID_LAYER);
		text.setFontSize(GraphicText.FONT_SIZE_LARGE);
		text.setJustified(GraphicText.MIDDLE_JUSTIFIED);
		creditScroll.add(text);
		for(String license:Library.licenses){
			text = new GraphicText("impact",license,Hub.MID_LAYER){
				{
					setFontSize(GraphicText.FONT_SIZE_LARGE);
					setJustified(GraphicText.MIDDLE_JUSTIFIED);
					for(int i=0;lines.get(i).getCharWidth()>0.6f;++i){
						String excess = lines.get(i).wrap(0.6f);
						if(!"".equals(excess)){
							getLine(i+1).change(excess);
						}
						resize(getWidth(),getHeight());
					}
					StringBuilder builder = new StringBuilder();
					for(int i=0;i<lines.size();++i){
						builder.append(lines.get(i).getText());
						builder.append("\n");
					}
					change(builder.toString());
					resize(getWidth(),getHeight());
				}
				@Override
				public void resize(float x, float y){
					super.resize(x, (lines.size()-1)*0.08f+0.07f);
				}
				@Override
				public void reposition(float x, float y){
					super.reposition(x, y+0.075f*(lines.size()-1));
				}
			};
			creditScroll.add(text);
			for(ITrack track:Library.tracks){
				if(track.getLicense().equals(license)){
					creditButton = new CreditButton(track.getName()+"\nby "+track.getArtist()+"\n"+track.getFeature(),track.getHttpLink());
					creditScroll.add(creditButton);
				}
			}
		}
		
		IconMenuButton button = new IconMenuButton("editor_arrows",3){
			{
				icon.resize(0.08f, 0.08f);
			}
			@Override
			public void performOnRelease(ClickEvent e){
				Hub.gui.setView(xyz);
			}
			@Override
			public void resize(float w, float h){
				super.resize(w, h);
				if(icon!=null){
					left.resize(w*0.5f,h);
					mid.resize(w*0.0f,h);
					right.resize(w*0.5f,h);			
					icon.resize(0.08f,0.08f);
				}
			}
		};
		button.resize(0.09f, 0.08f);
		button.reposition(0.03f,0.77f);
		addChild(button);
		
		for(GraphicEntity entity:creditScroll){
			addChild(entity);
			entity.resize(0.6f, 0.15f);
		}
	}
	private class CreditButton extends MenuButton{

		private int numberOfLines;
		private String link;
		public CreditButton(String text, String link) {
			super(text);
			numberOfLines+=text.split("\n").length;
			this.link = link;
		}
		@Override
		public float offsetY(int index){
			if(getChild(index)==text){
				return numberOfLines*0.075f-0.045f;
			}
			else return super.offsetY(index);
		}
		@Override
		public void resize(float x, float y){
			super.resize(x, numberOfLines*0.08f+0.07f);			
		}

		@Override
		public void performOnRelease(ClickEvent e){
			try {
				Hub.gui.openWebpage(new URL(link));
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public void update(double seconds){
		float currentScroll = scroll;
		int turnedOff = 0;
		for(int i=0;i<creditScroll.size();++i){
			currentScroll-=creditScroll.get(i).getHeight()+0.005f;
			creditScroll.get(i).reposition(0.2f, currentScroll);
			if(creditScroll.get(i).getY()+creditScroll.get(i).getHeight()>0.8f){
				creditScroll.get(i).setVisible(false);
				++turnedOff;
			}
			else {
				creditScroll.get(i).setVisible(true);
			}
		}
		scroll+=seconds*0.1f;
		if(turnedOff==creditScroll.size()){
			scroll = 0.5f;
		}
		super.update(seconds);
	}
}
