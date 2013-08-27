/**
 * 
 */
package org.jatusi.outputProjectTypes;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JWindow;

/**
 * @author Christopher Armenio
 *
 */
public abstract class OutputProjectType extends JPanel
{
	protected JRadioButton jrb;
	private JWindow optsWindow = null;
	
	
	/**
	 * Constructor for a simple OutputProjectType. The 
	 * OutputOptionPanel will be rendered as a simple
	 * JRadioButton with the provided name.
	 * 
	 * @param radioButtonStringIn the name of the OutputProjectType
	 * @param btnGrpIn the button group to which the rendered 
	 * 		radio button should belong
	 */
	public OutputProjectType(String radioButtonStringIn, ButtonGroup btnGrpIn)
	{
		this.jrb = new JRadioButton(radioButtonStringIn);
		btnGrpIn.add(this.jrb);
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(this.jrb);
	}
	
	
	/**
	 * Constructor for an OutputProjectType with a configurable
	 * options window. OutputOptionPanel will be rendered as a
	 * JRadioButton with the provided name followed by a button
	 * which will display the provided options window.
	 * 
	 * @param radioButtonStringIn the name of the OutputProjectType
	 * @param btnGrpIn the button group to which the rendered
	 * 		radio button should belong
	 * @param optsWindowIn the options window to be displayed as prompted
	 */
	public OutputProjectType(String radioButtonStringIn, ButtonGroup btnGrpIn, JWindow optsWindowIn)
	{
		this(radioButtonStringIn, btnGrpIn);
		this.optsWindow = optsWindowIn;
		
		JButton btnShowMoreOpts = new JButton("...");
		btnShowMoreOpts.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if( optsWindow != null ) optsWindow.setVisible(true);
			}
		});
	}
	
	
	/**
	 * Returns true if the radio button for this output project type is selected.
	 * @return true if this output type is selected, false if not.
	 */
	public boolean isSelected()
	{
		return this.jrb.isSelected();
	}
	
	
	/**
	 * Returns the name of this output project type
	 * @return the name of this output project type
	 */
	public String getName()
	{
		return this.jrb.getText();
	}
	
	
	public abstract boolean process(String projectNameIn, File outputDirIn, List<File> projectFilesIn);
	
}