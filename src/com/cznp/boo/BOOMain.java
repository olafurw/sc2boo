package com.cznp.boo;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;

public class BOOMain implements HotkeyListener, ActionListener
{
	private Properties config = null;
	
	private JFrame frame = null;
	
	private DirectoryLoader dirLoader = null;
	
	private JComboBox fileBox = null;
	
	private int nextMod = 0;
	private String nextModStr = null;
	private char nextKey = 0;
	
	private int resetMod = 0;
	private String resetModStr = null;
	private char resetKey = 0;
	
	private Label select = null;
	private Label hotkey = null;
	
	private Label nextHotkeys = null;
	private Label resetHotkeys = null;
	
	private JButton goButton = null;
	
	private ArrayList<String> buildOrder = null;
	private ArrayList<Label> buildOrderCurrent = null;
	private int buildOrderIndex = 0;
	
	private WindowDrag dragger = null;
	
    public static void main(String[] args)
    {
    	if(System.getProperty("os.arch").contains("64"))
    	{
    		JIntellitype.setLibraryLocation("JIntellitype64.dll");
    	}
    	else
    	{
    		JIntellitype.setLibraryLocation("JIntellitype.dll");
    	}
    	
        if (!JIntellitype.isJIntellitypeSupported())
        {
			System.out.println("JIntellitype.dll is not found in the path or this is not Windows 32bit OS.");
			System.exit(1);
		}
    	
    	new BOOMain();
    }
    
    public BOOMain()
    {
    	loadConfigFile();
    	
    	initWindowSettings();
    	initStartScreen();
        
        // Close window listener
        frame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(WindowEvent winEvt)
            {
            	JIntellitype.getInstance().cleanUp();
                System.exit(0); 
            }
        });
    }
    
    private void loadConfigFile()
    {
    	try
		{
    		config = new Properties();
			config.load(new FileInputStream("config.ini"));
			
			if(config.containsKey("nextMod") && config.containsKey("nextKey"))
			{
				nextModStr = config.getProperty("nextMod").toUpperCase();
				nextMod = stringToMod(nextModStr);
				nextKey = config.getProperty("nextKey").toUpperCase().charAt(0);
			}
			
			if(config.containsKey("nextMod") && config.containsKey("nextKey"))
			{
				resetModStr = config.getProperty("resetMod").toUpperCase();
				resetMod = stringToMod(resetModStr);
				resetKey = config.getProperty("resetKey").toUpperCase().charAt(0);
			}
		}
    	catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private int stringToMod(String mod)
    {
    	if(mod.equals("WIN"))
    	{
    		return JIntellitype.MOD_WIN;
    	}
    	
    	if(mod.equals("CTRL"))
    	{
    		return JIntellitype.MOD_CONTROL;
    	}
    	
    	if(mod.equals("ALT"))
    	{
    		return JIntellitype.MOD_ALT;
    	}
    	
    	return JIntellitype.MOD_SHIFT;
    }
    
    private void initWindowSettings()
    {
    	frame = new JFrame("BOO");
    	
    	frame.setUndecorated(true);
    	frame.getRootPane().setWindowDecorationStyle(JRootPane.COLOR_CHOOSER_DIALOG);
    	frame.setAlwaysOnTop(true);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(true);
        
        dragger = new WindowDrag(frame);
    }
    
    private String getFile()
    {
    	String filename = (String)fileBox.getSelectedItem();
    	
    	if(filename == null)
    	{
    		return "";
    	}
    	
    	return filename;
    }
	
	private void removeStartScreen()
	{
		frame.getContentPane().remove(select);
		frame.getContentPane().remove(fileBox);
		frame.getContentPane().remove(hotkey);
		frame.getContentPane().remove(nextHotkeys);
		frame.getContentPane().remove(resetHotkeys);
		frame.getContentPane().remove(goButton);
		
		frame.pack();
	}
	
	private void initStartScreen()
	{
		dirLoader = new DirectoryLoader();
        fileBox = new JComboBox();
        
        ArrayList<String> files = dirLoader.getList();
        
        for(String file: files)
        {
        	fileBox.addItem(file);
        }
        
        goButton = new JButton("Load");
        goButton.addActionListener(this);
        
        select = new Label("Select build order:");
        hotkey = new Label("Hotkey config:");
        
        nextHotkeys = new Label("Next item: " + nextModStr + "+" + nextKey);
        resetHotkeys = new Label("Reset build: " + resetModStr + "+" + resetKey);
        
        //select.addMouseMotionListener(dragger);
        //hotkey.addMouseMotionListener(dragger);
        //modKeyPanel.addMouseMotionListener(dragger);
        
        addStartScreen();
	}
	
	private void addStartScreen()
	{
		frame.getContentPane().setLayout(new GridLayout(6, 1));
		
		frame.getContentPane().add(select);
		frame.getContentPane().add(fileBox);
		frame.getContentPane().add(hotkey);
		frame.getContentPane().add(nextHotkeys);
		frame.getContentPane().add(resetHotkeys);
		frame.getContentPane().add(goButton);
		
		frame.pack();
	}
	
	private void registerGlobalKeys()
	{
    	JIntellitype.getInstance().addHotKeyListener(this);
    	JIntellitype.getInstance().registerHotKey(1, nextMod, (int)nextKey);
    	JIntellitype.getInstance().registerHotKey(2, resetMod, (int)resetKey);
	}
	
	private void initBuildScreen(String file)
	{
		File buildFile = new File("builds/" + file);
		
		if(buildFile.exists() && buildFile.canRead())
		{
			try
			{
				buildOrder = new ArrayList<String>();
				
				Scanner scanner = new Scanner(new FileInputStream("builds/" + file), "UTF-8");
				
				while(scanner.hasNextLine())
				{
					buildOrder.add(scanner.nextLine());
				}
				
				addBuildScreen();
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void addBuildScreen()
	{
		frame.getContentPane().setLayout(new GridLayout(3, 1));
		
		buildOrderCurrent = new ArrayList<Label>();
		
		buildOrderCurrent.add(new Label());
		buildOrderCurrent.add(new Label());
		buildOrderCurrent.add(new Label());
		
		if(buildOrder.size() >= 1)
		{
			buildOrderCurrent.get(0).setText(buildOrder.get(0));
		}
		buildOrderCurrent.get(0).setBackground(Color.DARK_GRAY);
		buildOrderCurrent.get(0).setForeground(Color.WHITE);
		//buildOrderCurrent.get(0).addMouseMotionListener(dragger);
		
		if(buildOrder.size() >= 2)
		{
			buildOrderCurrent.get(1).setText(buildOrder.get(1));
		}
		//buildOrderCurrent.get(1).addMouseMotionListener(dragger);
		
		if(buildOrder.size() >= 3)
		{
			buildOrderCurrent.get(2).setText(buildOrder.get(2));
		}
		//buildOrderCurrent.get(2).addMouseMotionListener(dragger);
		
		frame.getContentPane().add(buildOrderCurrent.get(0));
		frame.getContentPane().add(buildOrderCurrent.get(1));
		frame.getContentPane().add(buildOrderCurrent.get(2));
		frame.pack();
		
		frame.setSize(300, frame.getSize().height);
	}
	
	private void updateBuildOrderCurrent()
	{
		buildOrderIndex++;
		
		String textOne = "";
		String textTwo = "";
		String textThree = "";
		
		if(buildOrder.size() > buildOrderIndex)
		{
			textOne = buildOrder.get(buildOrderIndex);
		}
		if(buildOrder.size() > buildOrderIndex + 1)
		{
			textTwo = buildOrder.get(buildOrderIndex + 1);
		}
		if(buildOrder.size() > buildOrderIndex + 2)
		{
			textThree = buildOrder.get(buildOrderIndex + 2);
		}
		
		buildOrderCurrent.get(0).setText(textOne);
		buildOrderCurrent.get(1).setText(textTwo);
		buildOrderCurrent.get(2).setText(textThree);
	}
	
	private void resetBuildOrderCurrent()
	{
		buildOrderIndex = 0;
		
		String textOne = "";
		String textTwo = "";
		String textThree = "";
		
		if(buildOrder.size() > buildOrderIndex)
		{
			textOne = buildOrder.get(buildOrderIndex);
		}
		if(buildOrder.size() > buildOrderIndex + 1)
		{
			textTwo = buildOrder.get(buildOrderIndex + 1);
		}
		if(buildOrder.size() > buildOrderIndex + 2)
		{
			textThree = buildOrder.get(buildOrderIndex + 2);
		}
		
		buildOrderCurrent.get(0).setText(textOne);
		buildOrderCurrent.get(1).setText(textTwo);
		buildOrderCurrent.get(2).setText(textThree);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		removeStartScreen();
		registerGlobalKeys();
		
		initBuildScreen(getFile());
	}
	
	@Override
	public void onHotKey(int aIdentifier)
	{
		if(aIdentifier == 1)
		{
	        updateBuildOrderCurrent();
	    }
		if(aIdentifier == 2)
		{
	        resetBuildOrderCurrent();
	    }
	}
}
