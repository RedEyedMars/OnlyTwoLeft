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



public class GraphicRenderer {
	private float viewX,viewY,viewZ=0f;

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
					(1f+i)/length, 1f,		// top right	(V4)
					(1f+i)/length, 0		// bottom right	(V3)
			};

			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(12 * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			textureBuffer[t] = byteBuffer.asFloatBuffer();
			textureBuffer[t].put(textures);
			textureBuffer[t].position(0);
		}
		textureBuffers.put(k, textureBuffer);
	}
	public void display(){
		while(!toLoadtext.isEmpty()){
			loadText(toLoadtext.remove(0),true);
		}
		while(!Hub.addLayer.isEmpty()){
			Hub.drawLayer.add(Hub.addLayer.remove(0));
        }
		while(!Hub.removeLayer.isEmpty()){
			Hub.drawLayer.remove(Hub.removeLayer.remove(0));
        }
		GL11.glTranslatef(-0.7521f+viewX, -0.565f+viewY, -1.107f+viewZ);
		GL11.glScalef(1.51f, 1.0925f, 1f);
		animate = System.currentTimeMillis()-last>animationInterval;
		if(animate){
			last = System.currentTimeMillis();
		}
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glFrontFace(GL11.GL_CW);
		int previous = -1;
		GL11.glPushMatrix();
		for(int i=0;i<Hub.drawLayer.size();++i){
			drawTexture(Hub.drawLayer.get(i));
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

	public void clear() {
		throw new RuntimeException("CLEARLY NOT IMPLEMENETED (glrenderer.clear())");
	}
	public boolean buffersInclude(int key) {
		return textureBuffers.containsKey(key);
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
			for(String filename:fileBuilder.toString().split("\n")){
				if(filename.matches("\\s*"))continue;
				String[] args = filename.split("\\Q"+File.separator+"\\E");
				String name = args[1].substring(0,args[1].indexOf('.'));
				Integer size = Integer.parseInt(args[0]);
				loadImageFromPath("images/"+filename,size,name);
			}

			loaded = true;
		}
	}

	public boolean contains(String key) {
		return texMap.containsKey(key);
	}


	public void loadText(String text) {
		toLoadtext.add(text);
	}
	private void loadText(String text, boolean fixed){
		if(!contains("$"+text)){
			String name = "$"+text;
			if(!buffersInclude(1)){
				setupTextureBuffer(1);			
			}
			loadImageFromGLImage(
					GLFont.createCharImage(
							text, 
							new Font("Times New Roman", Font.PLAIN, 12), new float[]{1,1,1,1}, new float[]{0,0,0,0f}),
					name);
		}
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
		sizMap.put(name, 1);
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
}
