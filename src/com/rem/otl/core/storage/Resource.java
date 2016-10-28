package com.rem.otl.core.storage;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.rem.otl.core.game.Action;

public class Resource <Type> {
	
	public static final int INPUT_STREAM = 0;
	public static final int OUTPUT_STREAM = 1;
	public static final int FILE = 2;
	public static final int INTEGER = 3;
	public static final int BITMAP = 4;
	
	
	private String path;
	private String name;
	private int typeId;
	private Type res;
	private boolean exists = true;
	private Action<Resource<Type>> getMethod;


	public Resource(String name, String path,final Type res){
		this(name,path,
				(res instanceof InputStream)?INPUT_STREAM:
				(res instanceof OutputStream)?OUTPUT_STREAM:
				(res instanceof File)?FILE:
				(res instanceof Integer)?INTEGER:
				("Bitmap".equals(res.getClass().getSimpleName()))?BITMAP:
					-1,				
				new Action<Resource<Type>>(){

			@Override
			public void act(Resource<Type> subject) {
				subject.res = res;
			}});
	}
	public Resource(String name, String path,int type, Action<Resource<Type>> getMethod){
		this.typeId = type;
		this.path = path;
		this.name = name;
		this.getMethod = getMethod;
	}
	
	public Type get(){
		getMethod.act(this);
		return res;
	}
	public int type(){
		return typeId;
	}
	public String getPath(){
		return path;
	}
	public String getName(){
		return name;
	}
	
	public void setExists(boolean exists){
		this.exists = exists;
	}

	public boolean exists() {
		return exists;
	}
	public void set(Type res){
		this.res = res;
	}
}
