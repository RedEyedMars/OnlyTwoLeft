package gui.graphics;

import gui.gl.GLApp;
import gui.gl.GLImage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;


public class ImageColourer{
	/*
			new ImageColourer("parts_wrench",com.spacejumpers.R.drawable.parts_wrench,colours);
	private String name;
	private final Color[] colours;
	private final String toLoad;
	public ImageColourer(String name, String toLoad,Color...colours){
		this.colours = colours;
		this.toLoad = toLoad;
		this.name = name;
		CommonHub.renderer.initialize(name,2);
		this.run();
	}
	public void run(){
		if(!CommonHub.renderer.buffersInclude(colours.length)){
			CommonHub.renderer.setupTextureBuffer(colours.length);			
		}
		CommonHub.renderer.addGLImage(createImage(), name, colours.length);
	}
	public BufferedImage createImage() {
		Color bg = new Color(0,0,0,0);
		Color fg = new Color(1,1,1,1);
		boolean isAntiAliased = true;
		boolean usesFractionalMetrics = false;

		// get size of texture image needed to hold largest character of this font
		int imgSizeW = 32*colours.length;
		int imgSizeH = GLApp.getPowerOfTwoBiggerThan(32);
		if (imgSizeW > 2048) {
			GLApp.err("GLFont.createCharImage(): texture size will be too big (" + imgSizeW + ") Make the font size smaller.");
			return null;
		}

		// we'll draw text into this image
		BufferedImage image = new BufferedImage(imgSizeW, imgSizeH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		// Clear image with background color (make transparent if color has alpha value)
		if (bg.getAlpha() < 255) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, (float)bg.getAlpha()/255f));
		}
		g.setColor(bg);
		g.fillRect(0,0,imgSizeW,imgSizeH);

		// prepare to draw character in foreground color
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, isAntiAliased? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, usesFractionalMetrics? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		BufferedImage tile = GLImage.loadJavaImage(com.spacejumpers.R.getResource(toLoad));
		for(int i=0;i<colours.length;++i){
			g.drawImage(colourImage(tile,colours[i]),32*i,0,imgSizeW/colours.length, imgSizeH, null);
		}


		g.dispose();

		return image;
	}
	
	public static BufferedImage colourImage(BufferedImage sourceImage,Color c) {

		short[] red = new short[256];
		short[] green = new short[256];
		short[] blue = new short[256];
		short[] alpha = new short[256];
		for (short i = 0; i < 256; i++) {
		  green[i]=(short)(i*c.getGreen()/255);
		  red[i]=(short) (i*c.getRed()/255);
		  blue[i]=(short) (i*c.getBlue()/255);
		  alpha[i] = i;
		}
		short[][] data = new short[][] {
		    red, green, blue, alpha
		};

		LookupTable lookupTable = new ShortLookupTable(0, data);
		LookupOp op = new LookupOp(lookupTable, null);
		BufferedImage destinationImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		destinationImage = op.filter(sourceImage, destinationImage);
		return destinationImage;
	}*/
}
