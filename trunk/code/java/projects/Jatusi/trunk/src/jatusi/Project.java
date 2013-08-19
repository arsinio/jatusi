/**
 * 
 */
package jatusi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Christopher Armenio
 *
 */
public class Project
{
	List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
	
	
	protected void addCompilationUnit(CompilationUnit cuIn)
	{
		this.compilationUnits.add(cuIn);
	}
	
	
	protected boolean generateStubFiles(File targetDirectoryIn)
	{
		for( CompilationUnit currCu : this.compilationUnits )
		{
			// first, we have to generate our header file
			if( !CGenerator.generateHeaderFile(targetDirectoryIn, currCu) ) return false;
			
			// next, generate our implementation file (c file)
			if( !CGenerator.generateImplementationFile(targetDirectoryIn, currCu) ) return false;
		}
		
		
		return true;
	}
}
