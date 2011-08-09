package com.cznp.boo;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class WindowDrag implements MouseMotionListener
{
	private JFrame frame;
	
	public WindowDrag(JFrame frame)
	{
		this.frame = frame;
	}
	
	@Override
	public void mouseDragged(MouseEvent me)
	{
		//System.out.println(me.getX() + ":" + me.getY());
		frame.setLocation(me.getXOnScreen(), me.getYOnScreen());
	}

	@Override
	public void mouseMoved(MouseEvent me)
	{
		// NADA
	}
}
