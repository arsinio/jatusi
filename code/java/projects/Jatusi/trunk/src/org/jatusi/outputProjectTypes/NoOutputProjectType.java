/**
 * 
 */
package org.jatusi.outputProjectTypes;

import java.io.File;
import java.util.List;

import javax.swing.ButtonGroup;

/**
 * @author Christopher Armenio
 *
 */
public class NoOutputProjectType extends OutputProjectType
{
	public NoOutputProjectType(ButtonGroup btnGrpIn)
	{
		super("None", btnGrpIn);
		
		this.jrb.setSelected(true);
	}

	
	@Override
	public boolean process(String projectNameIn, File outputDirIn, List<File> projectFilesIn)
	{
		// nothing to do here!
		return true;
	}
}
