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
public class AtmelStudioOutputProjectType extends OutputProjectType
{
	public AtmelStudioOutputProjectType(ButtonGroup btnGrpIn)
	{
		super("Atmel Studio Project", btnGrpIn);
	}

	
	@Override
	public boolean process(String projectNameIn, File outputDirIn, List<File> projectFilesIn)
	{
		return false;
	}
}
