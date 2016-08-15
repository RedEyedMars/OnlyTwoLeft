package gui.graphics;

import gui.Gui;
import gui.gl.GLFont;
import gui.gl.GLImage;
import main.Hub;
import main.Log;

import java.awt.Color;
import java.awt.Font;
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

	private Map<String,ButtonAction> loadImageFromTextureName = new HashMap<String,ButtonAction>();
	private Map<String,Integer> texMap = new HashMap<String,Integer>();
	private Map<String,Integer> sizMap = new HashMap<String,Integer>();
	private Map<Integer,FloatBuffer[]> textureBuffers = new HashMap<Integer,FloatBuffer[]>();

	private List<String> toLoadtext = new ArrayList<String>();


	public boolean animate;
	private long last = System.currentTimeMillis();
	private boolean loaded = false;

	public long animationInterval = 100;

	public GraphicRenderer() {
	}

	public void setupTextureBuffer(int k) {
		float length = (float)k;
		FloatBuffer[] textureBuffer = new FloatBuffer[k];
		float textures[];
		for(int t=0;t<k;++t){
			float i = (float)(t%k);
			textures= new float[]{
					// Mapping coordinates for the vertices
					i/length, 1f,		// top left		(V2)
					i/length, 0,		// bottom left	(V1)
					(i+1)/length, 1f,		// top right	(V4)
					(i+1)/length, 0		// bottom right	(V3)
			};

			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2 * 4 * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			textureBuffer[t] = byteBuffer.asFloatBuffer();
			textureBuffer[t].put(textures);
			textureBuffer[t].position(0);
		}
		textureBuffers.put(k, textureBuffer);
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
		textureBuffers.put(xMax*yMax, textureBuffer);
	}
	public void display(){
		while(!Hub.addLayer.isEmpty()){
			GraphicElement e = Hub.addLayer.remove(0);
			if(loadImageFromTextureName.containsKey(e.getTextureName())){
				loadImageFromTextureName.remove(e.getTextureName()).act(null);
			}
			if(e.getLayer()==1){
				Hub.drawTopLayer.add(e);
			}
			else if(e.getLayer()==0){
				Hub.drawBotLayer.add(e);
			}			
		}
		while(!Hub.removeLayer.isEmpty()){
			GraphicElement e = Hub.removeLayer.remove(0);
			if(e.getLayer()==1){
				Hub.drawTopLayer.remove(e);
			}
			else if(e.getLayer()==0){
				Hub.drawBotLayer.remove(e);
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
		for(int i=0;i<Hub.drawBotLayer.size();++i){
			drawTexture(Hub.drawBotLayer.get(i));
		}
		for(int i=0;i<Hub.drawTopLayer.size();++i){
			drawTexture(Hub.drawTopLayer.get(i));
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
		GL11.glTexCoordPointer(2, 0, textureBuffers.get(sizMap.get(d.getTextureName()))[d.textureIndex()]);
		d.draw();
	}

	public boolean buffersInclude(int key) {
		return textureBuffers.containsKey(key);
	}

	public void loadImages(){
		if(!loaded ){
			if(!buffersInclude(8*8)){
				setupTextureBuffer(8,8);
			}
			if(!buffersInclude(16*16)){
				setupTextureBuffer(16,16);
			}
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
			for(final String filename:fileBuilder.toString().split("\n")){
				if(filename.matches("\\s*"))continue;
				String[] args = filename.split("\\Q/\\E");
				final String name = args[1].substring(0,args[1].indexOf('.'));
				final Integer size = Integer.parseInt(args[0]);
				loadImageFromTextureName.put(name,new ButtonAction(){
					@Override
					public void act(Editor subject) {
						loadImageFromPath("images/"+filename,size,name);
					}				
				});
			}
			loadImageFromTextureName.put("timesnewroman",new ButtonAction(){
				@Override
				public void act(Editor subject) {					
					loadText("timesnewroman",new Font("Times New Roman", Font.PLAIN, 16),16,new float[]{0f,0.75f,0.75f,1}, new float[]{0,0,0,0f});
				}});
			loadImageFromTextureName.put("impact",new ButtonAction(){
				@Override
				public void act(Editor subject) {
					loadText("impact",new Font("Cooper Black", Font.PLAIN, 32),32,new float[]{0f,0f,0f,1}, new float[]{0,0,0,0f});
				}});
			loadImageFromTextureName.put("impactWhite",new ButtonAction(){
				@Override
				public void act(Editor subject) {
					loadText("impactWhite",new Font("Cooper Black", Font.PLAIN, 32),32,new float[]{1f,1f,1f,1}, new float[]{0,0,0,0f});
				}});

			loaded = true;
		}
	}

	public boolean contains(String key) {
		return texMap.containsKey(key);
	}

	public Map<String,List<Float>> letterWidths= new HashMap<String,List<Float>>();
	private void loadText(String fontName,Font font, int size, float[] foregroundColour, float[] backgroundColour){
		letterWidths.put(fontName, new ArrayList<Float>());
		loadImageFromGLImage(
				GLFont.createCharImage(
						fontName, 
						font,size, foregroundColour, backgroundColour),
				"$"+fontName,
				16*16);
	}

	public void loadImageFromPath(String path, int size, String name){
		if(!buffersInclude(size)){
			setupTextureBuffer(size);			
		}
		int tex = Gui.makeTexture(R.getResource(path));
		texMap.put(name, tex);
		sizMap.put(name, size);
	}
	public void loadImageFromExternalPath(String path, int size, String name){
		if(!buffersInclude(size)){
			setupTextureBuffer(size);			
		}
		int tex = Gui.makeTexture(path);
		texMap.put(name, tex);
		sizMap.put(name, size);
	}
	public void loadImageFromGLImage(BufferedImage img, String name){
		GLImage textureImg = new GLImage(img);
		int tex = 0;
		if (textureImg != null) {
			tex = Gui.makeTexture(textureImg);
		}
		texMap.put(name, tex);
		sizMap.put(name, 1);
	}
	public void loadImageFromGLImage(BufferedImage img, String name, int size){
		GLImage textureImg = new GLImage(img);
		int tex = 0;
		if (textureImg != null) {
			tex = Gui.makeTexture(textureImg);
		}
		texMap.put(name, tex);
		sizMap.put(name, size);
	}
	public void deleteTexture(String textureName) {
		sizMap.remove(textureName);
		GL11.glDeleteTextures(texMap.remove(textureName));
	}

	public void reloadTexture(String textureName, String texturePath, int newSize){
		sizMap.remove(textureName);
		GL11.glDeleteTextures(texMap.remove(textureName));
		loadImageFromPath(texturePath,newSize,textureName);
	}	

	public void reloadExternalTexture(String textureName, String texturePath, int newSize){
		sizMap.remove(textureName);
		GL11.glDeleteTextures(texMap.remove(textureName));
		loadImageFromExternalPath(texturePath,newSize,textureName);
	}
	public void translate(float x, float y, float z){
		viewX+=x;
		viewY+=y;
		viewZ+=z;
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
}
