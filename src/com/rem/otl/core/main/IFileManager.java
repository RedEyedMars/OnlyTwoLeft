package com.rem.otl.core.main;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.rem.otl.core.storage.Resource;

public interface IFileManager {

	
	public Resource<InputStream> createInputStream(String path);
	public Resource<OutputStream> createOutputStream(String path);
	public Resource createImageResource(String name, String path);
	public boolean deleteFile(String string);
	public void createDirectory(String string);
	public File getDirectory(String path);
}
