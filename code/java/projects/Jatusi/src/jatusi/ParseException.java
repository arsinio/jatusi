/**
 * 
 */
package jatusi;

import java.io.File;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Christopher Armenio
 *
 */
public class ParseException extends Exception
{

	public ParseException(String errorMsgIn, ASTNode generatingNodeIn)
	{
		super(	String.format("%s -- %s:%d", 
				errorMsgIn, 
				((((CompilationUnit)generatingNodeIn.getRoot()).getProperty(JavaParser.CU_PROPERTY_SOURCE_FILE) == null) ? "<unknown>" : (((CompilationUnit)generatingNodeIn.getRoot()).getProperty(JavaParser.CU_PROPERTY_SOURCE_FILE))), 
				((CompilationUnit)generatingNodeIn.getRoot()).getLineNumber(generatingNodeIn.getStartPosition()))
			 );
	}
}
