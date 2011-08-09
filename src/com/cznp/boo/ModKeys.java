package com.cznp.boo;

public class ModKeys
{
	private int id;
	private String name;
	
	public ModKeys(int id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		return name;
	}
}
