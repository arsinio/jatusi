package org.jatusi;


import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import org.jatusi.gui.JatusiGui;


/**
 * @author Christopher Armenio
 *
 */
public class Jatusi
{
	private static Properties prop = new Properties();
	
	
	public static Properties getProperties()
	{
		return prop;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// set our default look and feel
		try{ UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch(Exception e){ e.printStackTrace(); }
		
		// read our properties
		try
		{
			prop.load(Jatusi.class.getClassLoader().getResourceAsStream("jatusi.properties"));
		}
		catch (Exception e)
		{
			System.err.println("Could not load 'jatusi.properties'...using default configuration...");
		}
		

		//Launch the application.
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					JatusiGui window = new JatusiGui();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

}
