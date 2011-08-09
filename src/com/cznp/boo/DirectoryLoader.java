package com.cznp.boo;

import java.io.File;
import java.util.ArrayList;

public class DirectoryLoader
{
	private String directoryName = null;
	
	public DirectoryLoader()
	{
		directoryName = "builds";
	}
	
	public ArrayList<String> getList()
	{
		ArrayList<String> list = new ArrayList<String>();
		
		File dir = new File(directoryName);
		
		for(File file: dir.listFiles())
		{
			list.add(file.getName());
		}
		
		return list;
	}
}
