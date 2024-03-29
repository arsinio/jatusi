/**
 * 
 */
package org.jatusi;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Christopher Armenio
 *
 */
public class JavaParser
{
	public static final String CU_PROPERTY_SOURCE_FILE = "CU_PROP_SOURCE_FILE";
	
	
	private static ASTParser parser = ASTParser.newParser(AST.JLS4);
	
	
	public static Project parseProject(List<File> filesIn)
	{
		Project retProject = new Project();
		
		// now iterate over each file/class
		for( File currFile : filesIn )
		{	
			// read the file contents
			String fileContents = null;
			try
			{
				fileContents = FileUtils.readFileToString(currFile);
			}
			catch (IOException e)
			{
				System.err.printf("Error reading file '%s'\r\n", currFile.getName());
				return null;
			}
			
			// now parse the AST
			parser.setSource(fileContents.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			// check to see if the compilation unit has problems
			IProblem problems [] = cu.getProblems();
			if( problems.length != 0 )
			{
				System.err.printf("File '%s' has errors:\r\n", currFile.getName());
				for( IProblem currProb : problems )
				{
					System.err.printf("   line %d: '%s'\r\n", currProb.getSourceLineNumber(), currProb.getMessage());
				}
				return null;
			}
			
			// AST is successfully parsed...now save the file from which this CU came
			cu.setProperty(CU_PROPERTY_SOURCE_FILE, currFile);
			
			// now add the compilation unit to the project
			System.out.printf("Successfully parsed '%s'\r\n", currFile.getName());
			retProject.addCompilationUnit(cu);
		}
		
		return retProject;
	}
}
