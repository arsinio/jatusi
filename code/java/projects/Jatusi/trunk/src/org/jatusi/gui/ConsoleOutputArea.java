/**
 * 
 */
package org.jatusi.gui;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author Christopher Armenio
 *
 */
public class ConsoleOutputArea extends JScrollPane
{
	public PrintStream out = null;
	public PrintStream err = null;
	
	private JTextPane textPane = new JTextPane();
	private StyledDocument doc = this.textPane.getStyledDocument();
	private Style outStyle = this.textPane.addStyle("outStyle",  null);
	private Style errStyle = this.textPane.addStyle("errStyle", null);
	
	
	public ConsoleOutputArea()
	{
		StyleConstants.setForeground(this.outStyle, Color.BLACK);
		StyleConstants.setForeground(this.errStyle, Color.RED);

		this.setViewportView(this.textPane);
		this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.textPane.setEditable(false);
		
		// generate an output stream to which our process can print
		this.out = new PrintStream(new OutputStream()
		{
			@Override
			public void write(int byteIn) throws IOException 
			{
				try
				{
					doc.insertString(doc.getLength(), String.valueOf((char)byteIn), outStyle);
					textPane.setCaretPosition(doc.getLength());
				}
				catch (BadLocationException e){}
			}
		});
		
		// generate an error stream to which our process can print
		this.err= new PrintStream(new OutputStream()
		{
			@Override
			public void write(int byteIn) throws IOException 
			{
				try
				{
					doc.insertString(doc.getLength(), String.valueOf((char)byteIn), errStyle);
					textPane.setCaretPosition(doc.getLength());
				}
				catch (BadLocationException e){}
			}
		});
	}
	
	
	public void clear()
	{
		this.textPane.setText("");
	}
}
