package jatusi;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.UIManager;


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
			
		
		/*
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Jatusi -- Choose Project Directory");
		fileChooser.setCurrentDirectory(new File("C:\\Users\\Christopher Armenio\\workspace\\Jatusi"));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if( fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION )
		{
			Project newProject = JavaParser.parseProject(fileChooser.getSelectedFile());
		}
		else
		{
			System.err.println("Error choosing project directory...");
		}
		*/
	
		Project newProject = JavaParser.parseProject(new File("C:\\Users\\Christopher Armenio\\workspace\\Jatusi Test Project"));
		newProject.generateStubFiles(new File("C:\\Users\\Christopher Armenio\\Desktop\\test"));
	}

}
