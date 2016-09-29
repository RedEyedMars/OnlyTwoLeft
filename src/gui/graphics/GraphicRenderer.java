package gui.graphics;

import gui.Gui;
import gui.gl.GLApp;
import gui.gl.GLImage;
import gui.inputs.MotionEvent;
import main.Hub;
import main.Log;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import editor.ButtonAction;
import editor.Editor;
import game.Action;



public class GraphicRenderer {
	private float viewX,viewY,viewZ=0f;

	private List<GraphicElement> addLayer = new ArrayList<GraphicElement>();
	private List<GraphicElement> removeLayer = new ArrayList<GraphicElement>();
	private List<GraphicElement> drawBotLayer = new ArrayList<GraphicElement>();
	private List<GraphicElement> drawTopLayer = new ArrayList<GraphicElement>();

	private Map<String,ButtonAction> loadImageFromTextureName = new HashMap<String,ButtonAction>();
	private Map<String,Integer> texMap = new HashMap<String,Integer>();
	private Map<String,String> sizMap = new HashMap<String,String>();
	private Map<String,FloatBuffer[]> textureBuffers = new HashMap<String,FloatBuffer[]>();

	private List<String> toLoadtext = new ArrayList<String>();


	public boolean animate;
	private long last = System.currentTimeMillis();
	private boolean loaded = false;

	public long animationInterval = 100;

	public GraphicRenderer() {
	}
	public void setupTextureBuffer(int xMax, int yMax) {
		float length = (float)xMax;
		float height = (float)yMax;
		FloatBuffer[] textureBuffer = new FloatBuffer[xMax*yMax];
		float textures[];
		for(int y=0;y<yMax;++y){
			for(int x=0;x<xMax;++x){
				textures= new float[]{
						// Mapping coordinates for the vertices
						x/length, (y+1)/height,		// top left		(V2)
						x/length, (y)/height,		// bottom left	(V1)
						(x+1)/length, (y+1)/height,		// top right	(V4)
						(x+1)/length, (y)/height		// bottom right	(V3)
				};

				ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2 * 4 * 4);
				byteBuffer.order(ByteOrder.nativeOrder());
				textureBuffer[y*xMax+x] = byteBuffer.asFloatBuffer();
				textureBuffer[y*xMax+x].put(textures);
				textureBuffer[y*xMax+x].position(0);
			}
		}
		textureBuffers.put(xMax+"x"+yMax, textureBuffer);
	}
	public void display(){

		while(!addLayer.isEmpty()){
			synchronized(addLayer){
				GraphicElement e = addLayer.remove(0);
				if(loadImageFromTextureName.containsKey(e.getTextureName())){
					loadImageFromTextureName.remove(e.getTextureName()).act(null);
				}
				if(e.getLayer()==1){
					drawTopLayer.add(e);
				}
				else if(e.getLayer()==0){
					drawBotLayer.add(e);
				}
			}
		}
		while(!removeLayer.isEmpty()){
			synchronized(removeLayer){
				GraphicElement e = removeLayer.remove(0);
				if(e==null)continue;
				if(e.getLayer()==1){
					drawTopLayer.remove(e);
				}
				else if(e.getLayer()==0){
					drawBotLayer.remove(e);
				}
			}
		}
		//GL11.glTranslatef(-0f, -0f, -1f);
		GL11.glTranslatef(-0.7521f+viewX, -0.565f+viewY, -1.107f+viewZ);
		GL11.glScalef(1.504f, 1.12875f, 1f);
		//GL11.glScalef(0.5f, 0.5f, 1f);
		animate = System.currentTimeMillis()-last>animationInterval;
		if(animate){
			last = System.currentTimeMillis();
		}
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glFrontFace(GL11.GL_CW);
		int previous = -1;
		GL11.glPushMatrix();
		for(int i=0;i<drawBotLayer.size();++i){
			drawTexture(drawBotLayer.get(i));
		}
		for(int i=0;i<drawTopLayer.size();++i){
			drawTexture(drawTopLayer.get(i));
		}
		GL11.glPopMatrix();
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

	}

	private int previousTexture = -1;
	public void drawTexture(GraphicElement d){
		if(!texMap.containsKey(d.getTextureName())){
			System.err.println(d.getTextureName()+" not a recognized texture name");
		}
		if(previousTexture != texMap.get(d.getTextureName())){
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texMap.get(d.getTextureName()));
			previousTexture = texMap.get(d.getTextureName());
		}
		GL11.glTexCoordPointer(2, 0, textureBuffers.get(sizMap.get(d.getTextureName()))[d.getFrame()]);
		d.draw();
	}

	public boolean buffersInclude(int sizeX, Integer sizeY) {
		return textureBuffers.containsKey(sizeX+"x"+sizeY);
	}

	public void addElement(GraphicElement e){
		synchronized(addLayer){
			addLayer.add(e);
		}
	}
	public void removeElement(GraphicElement e){
		synchronized(removeLayer){
			removeLayer.add(e);
		}
	}

	public void clearAdditions(){
		addLayer.clear();
	}
	public void loadImages(){
		if(!loaded ){
			InputStream url = R.class.getResourceAsStream("images/image.list");
			StringBuilder fileBuilder = new StringBuilder();
			try {
				int c = url.read();
				while(c!=-1){
					fileBuilder.append((char)c);
					c = url.read();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			int currentSizeX = 1;
			int currentSizeY = 1;
			for(final String line:fileBuilder.toString().split("\n")){
				if(line.matches("\\s*"))continue;

				StringBuilder lineBuilder = new StringBuilder();
				for(char c:line.toCharArray()){
					if(c>31){
						lineBuilder.append(c);
					}
				}
				final String filename = lineBuilder.toString();
				if(filename.matches("\\d+:")){
					currentSizeX = Integer.parseInt(filename.substring(0,filename.length()-1));
					currentSizeY = 1;
				}
				else if(filename.matches("\\d+x\\d+:")){
					currentSizeX = Integer.parseInt(filename.substring(0,filename.indexOf('x')));
					currentSizeY = Integer.parseInt(filename.substring(filename.indexOf('x')+1,filename.length()-1));
				}
				else {
					final String name = filename.substring(0,filename.lastIndexOf('.'));
					final Integer sizeX = currentSizeX;
					final Integer sizeY = currentSizeY;
					final String imageFilename = currentSizeY==1?("images/"+sizeX+"/"+name+".png"):("images/"+sizeX+"x"+sizeY+"/"+name+".png");

					loadImageFromTextureName.put(name,new ButtonAction(){
						@Override
						public void act(MotionEvent event) {
							loadImageFromPath(imageFilename,sizeX,sizeY,name);
						}				
					});
				}
			}
			loadImageFromTextureName.put("timesnewroman",new ButtonAction(){
				@Override
				public void act(MotionEvent event) {					
					loadText("timesnewroman",new Font("Times New Roman", Font.PLAIN, 16),16,new float[]{0f,0.75f,0.75f,1}, new float[]{0,0,0,0f});
				}});
			loadText("impact",new Font("Cooper Black", Font.PLAIN, 32),32,new float[]{0f,0f,0f,1}, new float[]{0,0,0,0f});
			loadText("impactWhite",new Font("Cooper Black", Font.PLAIN, 32),32,new float[]{1f,1f,1f,1}, new float[]{0,0,0,0f});


			loaded = true;
		}
	}

	public boolean contains(String key) {
		return texMap.containsKey(key);
	}

	public Map<String,List<Float>> letterWidths= new HashMap<String,List<Float>>();
	private void loadText(String fontName,Font font, int size, float[] foregroundColour, float[] backgroundColour){
		if(!buffersInclude(16,16)){
			setupTextureBuffer(16,16);			
		}
		letterWidths.put(fontName, new ArrayList<Float>());
		loadImageFromGLImage(
				createCharImage(
						fontName, 
						font,size, foregroundColour, backgroundColour),
				"$"+fontName,
				16,16);
	}

	public void loadImageFromPath(String path, int sizeX, Integer sizeY, String name){
		if(!buffersInclude(sizeX,sizeY)){
			setupTextureBuffer(sizeX,sizeY);			
		}
		int tex = Gui.makeTexture(R.getResource(path));
		texMap.put(name, tex);
		sizMap.put(name, sizeX+"x"+sizeY);
	}
	public void loadImageFromExternalPath(String path, int sizeX, Integer sizeY, String name){
		if(!buffersInclude(sizeX,sizeY)){			
			setupTextureBuffer(sizeX,sizeY);			
		}
		int tex = Gui.makeTexture(path);
		texMap.put(name, tex);
		sizMap.put(name, sizeX+"x"+sizeY);
	}
	public void loadImageFromGLImage(BufferedImage img, String name){
		GLImage textureImg = new GLImage(img);
		int tex = 0;
		if (textureImg != null) {
			tex = Gui.makeTexture(textureImg);
		}
		texMap.put(name, tex);
		sizMap.put(name, "1x1");
	}
	public void loadImageFromGLImage(BufferedImage img, String name, int sizeX, int sizeY){
		GLImage textureImg = new GLImage(img);
		int tex = 0;
		if (textureImg != null) {
			tex = Gui.makeTexture(textureImg);
		}
		texMap.put(name, tex);
		sizMap.put(name, sizeX+"x"+sizeY);
	}
	public void deleteTexture(String textureName) {
		sizMap.remove(textureName);
		GL11.glDeleteTextures(texMap.remove(textureName));
	}

	public void reloadTexture(String textureName, String texturePath, int newSizeX, int newSizeY){
		sizMap.remove(textureName);
		GL11.glDeleteTextures(texMap.remove(textureName));
		loadImageFromPath(texturePath,newSizeX,newSizeY,textureName);
	}	

	public void reloadExternalTexture(String textureName, String texturePath, int newSizeX, int newSizeY){
		sizMap.remove(textureName);
		GL11.glDeleteTextures(texMap.remove(textureName));
		loadImageFromExternalPath(texturePath,newSizeX,newSizeY,textureName);
	}
	public void translate(float x, float y, float z){
		viewX+=x;
		viewY+=y;
		viewZ+=z;
	}
	public void translateTo(float x, float y){
		viewX=x;
		viewY=y;
	}

	public float getViewX() {
		return viewX;
	}
	public float getViewY() {
		return viewY;
	}

	public void loadFont(String font) {
		if(loadImageFromTextureName.containsKey(font)){
			loadImageFromTextureName.remove(font).act(null);
		}
	}

	/**
	 * return a BufferedImage containing the given character drawn with the given font.
	 * Character will be drawn on its baseline, and centered horizontally in the image.
	 * 
	 * @param text     a single character to render
	 * @param font     the font to render with
	 * @param fgColor  foreground (text) color as rgb or rgba values in range 0-1
	 * @param bgColor  background color as rgb or rgba values in range 0-1 (set alpha to 0 to make transparent)
	 * @return
	 */
	public static BufferedImage createCharImage(String fontName, Font font, int size, float[] fgColor, float[] bgColor) {
		Color bg = bgColor==null? new Color(0,0,0,0) : (bgColor.length==3? new Color(bgColor[0],bgColor[1],bgColor[2],1) : new Color(bgColor[0],bgColor[1],bgColor[2],bgColor[3]));
		Color fg = fgColor==null? new Color(1,1,1,1) : (fgColor.length==3? new Color(fgColor[0],fgColor[1],fgColor[2],1) : new Color(fgColor[0],fgColor[1],fgColor[2],fgColor[3]));
		boolean isAntiAliased = true;
		boolean usesFractionalMetrics = false;

		// get size of texture image needed to hold largest character of this font
		//int maxCharSize = getFontSize(font);
		int imgSizeW = size*16;
		int imgSizeH = size*16;
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
		g.setColor(fg);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, isAntiAliased? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, usesFractionalMetrics? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		// place the character (on baseline, centered horizontally)
		FontMetrics fm = g.getFontMetrics();
		int cwidth = fm.charWidth('M');
		int height = fm.getHeight();
		int ascent = fm.getAscent();
		int hborder = 2;
		int vborder = height-ascent/2-1;

		char[] data = new char[128];
		for(int i=0;i<128;++i){
			data[i]=((char)i);
		}
		int index = 0;
		for(int y=0;y<8;++y){
			for(int x=0;x<16;++x){
				Hub.renderer.letterWidths.get(fontName).add((float) (fm.charWidth(data[index]))/cwidth);
				g.drawChars(data, index, 1, hborder+x*size, vborder+y*size+1);
				++index;
			}
		}

		g.dispose();
		return image;
	}
}
