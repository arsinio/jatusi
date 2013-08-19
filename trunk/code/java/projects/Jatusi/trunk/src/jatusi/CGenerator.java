/**
 * 
 */
package jatusi;

import java.beans.Introspector;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


/**
 * A Class that takes a Compilation Unit (typically a Java Class)
 * and can generate various C files / structures.
 * 
 * @author Christopher Armenio
 */
public class CGenerator
{	
	private static final String CU_PROPERTY_CONSTRUCTOR_LIST = "CU_PROP_CONSTRUCTOR_LIST";
	
	private static Map<PrimitiveType.Code, String> primitiveTypeMap = new HashMap<PrimitiveType.Code, String>(); 
	static
	{
		// generate our primitive type mappings from our config file (or use defaults)
		Properties prop = Jatusi.getProperties();
		primitiveTypeMap.put(PrimitiveType.BYTE, prop.getProperty(		"jatusi.java.typeMap.primitive.byte", 		"uint8_t"));
		primitiveTypeMap.put(PrimitiveType.SHORT, prop.getProperty(		"jatusi.java.typeMap.primitive.short",		"uint16_t"));
		primitiveTypeMap.put(PrimitiveType.CHAR, prop.getProperty(		"jatusi.java.typeMap.primitive.char", 		"char"));
		primitiveTypeMap.put(PrimitiveType.INT, prop.getProperty(		"jatusi.java.typeMap.primitive.int", 		"int32_t"));
		primitiveTypeMap.put(PrimitiveType.LONG, prop.getProperty(		"jatusi.java.typeMap.primitive.long", 		"int32_t"));
		primitiveTypeMap.put(PrimitiveType.FLOAT, prop.getProperty(		"jatusi.java.typeMap.primitive.float", 		"float"));
		primitiveTypeMap.put(PrimitiveType.DOUBLE, prop.getProperty(	"jatusi.java.typeMap.primitive.double", 	"double"));
		primitiveTypeMap.put(PrimitiveType.BOOLEAN, prop.getProperty(	"jatusi.java.typeMap.primitive.boolean",	"bool"));
		primitiveTypeMap.put(PrimitiveType.VOID, prop.getProperty(		"jatusi.java.typeMap.primitive.void", 		"void"));
	}
	
	
	private static Map<String, String> builtInTypeMap = new HashMap<String, String>(); 
	static
	{
		// generate our primitive type mappings from our config file (or use defaults)
		Properties prop = Jatusi.getProperties();
		builtInTypeMap.put("Byte", prop.getProperty(		"jatusi.java.typeMap.builtIn.Byte", 		"uint8_t"));
		builtInTypeMap.put("Short", prop.getProperty(		"jatusi.java.typeMap.builtIn.Short",		"uint16_t"));
		builtInTypeMap.put("Integer", prop.getProperty(		"jatusi.java.typeMap.builtIn.Integer", 		"int32_t"));
		builtInTypeMap.put("Long", prop.getProperty(		"jatusi.java.typeMap.builtIn.Long", 		"int32_t"));
		builtInTypeMap.put("Float", prop.getProperty(		"jatusi.java.typeMap.builtIn.Float", 		"float"));
		builtInTypeMap.put("Double", prop.getProperty(		"jatusi.java.typeMap.builtIn.Double", 		"double"));
		builtInTypeMap.put("Boolean", prop.getProperty(		"jatusi.java.typeMap.builtIn.Boolean",		"bool"));
	}
	
	
	/**
	 * This function generates the header file for the given compilation unit
	 * (should be a java class) and outputs the header file in the given
	 * target directory.
	 * 
	 * @Note: Filenames will be generated using the following format: namespace_className.h
	 * 
	 * @param targetDirectoryIn the directory in which the output header file should be placed
	 * @param cuIn the top-level compilation unit that was generated by parsing a java class
	 * 
	 * @return true if the header file was generated without any errors. False on error
	 */
	protected static boolean generateHeaderFile(File targetDirectoryIn, CompilationUnit cuIn)
	{
		// figure out our namespace
		String namespace = getNamespace(cuIn);
		
		// get the source file for this compilation unit
		File srcFile_raw = (File)cuIn.getProperty(JavaParser.CU_PROPERTY_SOURCE_FILE);
		String srcFile_string = (srcFile_raw == null) ? "<unknown>" : srcFile_raw.getName();
		
		// now get a reference to our type declaration for ease of use
		List<TypeDeclaration> types = cuIn.types();
		if( types.size() == 0 )
		{
			System.err.printf("No type declaration in compilation unit: '%s'\r\n", srcFile_string);
			return false;
		}
		else if( types.size() != 1 )
		{
			System.err.printf("Cannot parse a compilation unit with multiple types: '%s'\r\n", srcFile_string);
			return false;
		}
		TypeDeclaration tdIn = types.get(0);
		
		// now figure out our class name
		String className = getClassName(tdIn);
		
		// open our header file and start outputting our text
		try
		{
			PrintStream headerFile = new PrintStream(new File(targetDirectoryIn, String.format("%s_%s.h", namespace, className)));
		
			// now output our multiple inclusion protector
			headerFile.println(String.format("#ifndef %s_%s", namespace.toUpperCase(), className.toUpperCase()));
			headerFile.println(String.format("#define %s_%s", namespace.toUpperCase(), className.toUpperCase()));
			
			// now output our global macros (looking for public or protected "static final" vars)
			headerFile.println("\r\n\r\n// ******** global macro declarations ********");
			for( BodyDeclaration currBd : (List<BodyDeclaration>)tdIn.bodyDeclarations() )
			{
				// only look for member variables
				if( currBd instanceof FieldDeclaration )
				{
					FieldDeclaration currFd = (FieldDeclaration)currBd;
					if( (((currFd.getModifiers() & Modifier.STATIC) != 0) && ((currFd.getModifiers() & Modifier.FINAL) != 0)) &&
						(((currFd.getModifiers() & Modifier.PUBLIC) != 0) || ((currFd.getModifiers() & Modifier.PROTECTED) != 0)) )
					{
						// we have a "public|protected static final" variable...render as macro
						headerFile.println(renderMacro(currFd));
					}
					
				}
			}
			
			
			// now output our type declarations
			headerFile.println("\r\n\r\n// ******** global type declarations ********");
			// forward declaration for any callbacks
			headerFile.printf("typedef struct %s %s_t;\r\n", className, className);
			// @TODO other typedefs here
			
			// the actual class/struct declaration
			headerFile.printf("struct %s\r\n{\r\n", className);
			for( BodyDeclaration currBd : (List<BodyDeclaration>)tdIn.bodyDeclarations() )
			{
				// only look for member variables
				if( currBd instanceof FieldDeclaration )
				{
					FieldDeclaration currFd = (FieldDeclaration)currBd;
					if( (((currFd.getModifiers() & Modifier.STATIC) == 0) && ((currFd.getModifiers() & Modifier.FINAL) == 0)) )
					{
						headerFile.printf("   %s;\r\n", renderVariableDeclaration((FieldDeclaration)currBd) );
					}
				}
			}
			headerFile.println("};");
			
			
			// now output our member function prototypes (everything but private)
			headerFile.println("\r\n\r\n// ******** global function prototypes ********");
			for( BodyDeclaration currBd: (List<BodyDeclaration>)tdIn.bodyDeclarations() )
			{
				// only look for method declarations
				if( currBd instanceof MethodDeclaration )
				{
					MethodDeclaration currMd = (MethodDeclaration)currBd;
					
					// render everything but private
					if( (currMd.getModifiers() & Modifier.PRIVATE) == 0 )
					{
						headerFile.printf("%s;\r\n", renderMethodPrototype(currMd, namespace, className));	
					}
				}
			}
				
			
			// close out our multiple inclusion protector
			headerFile.println("\r\n\r\n#endif");
			headerFile.close();
		}
		catch(Exception e)
		{
			System.err.printf("While generating header file: '%s'\r\n", e.getMessage());
			return false;
		}
		
		
		return true;
	}
	
	
	/**
	 * This function generates the implementation file for the given compilation unit
	 * (should be a java class) and outputs the implementation file in the given
	 * target directory.
	 * 
	 * @Note: Filenames will be generated using the following format: namespace_className.c
	 * 
	 * @param targetDirectoryIn the directory in which the output header file should be placed
	 * @param cuIn the top-level compilation unit that was generated by parsing a java class
	 * 
	 * @return true if the implementation file was generated without any errors. False on error
	 */
	protected static boolean generateImplementationFile(File targetDirectoryIn, CompilationUnit cuIn)
	{
		// figure out our namespace
		String namespace = getNamespace(cuIn);
		
		// get the source file for this compilation unit
		File srcFile_raw = (File)cuIn.getProperty(JavaParser.CU_PROPERTY_SOURCE_FILE);
		String srcFile_string = (srcFile_raw == null) ? "<unknown>" : srcFile_raw.getName();
		
		// now get a reference to our type declaration for ease of use
		List<TypeDeclaration> types = cuIn.types();
		if( types.size() == 0 )
		{
			System.err.printf("No type declaration in compilation unit: '%s'\r\n", srcFile_string);
			return false;
		}
		else if( types.size() != 1 )
		{
			System.err.printf("Cannot parse a compilation unit with multiple types: '%s'\r\n", srcFile_string);
			return false;
		}
		TypeDeclaration tdIn = types.get(0);
		
		// now figure out our class name
		String className = getClassName(tdIn);
		
		// open our c file and start outputting our text
		try
		{
			PrintStream cFile = new PrintStream(new File(targetDirectoryIn, String.format("%s_%s.c", namespace, className)));
		
			// include our header file
			cFile.printf("#include \"%s\"\r\n", String.format("%s_%s.h", namespace, className));
			
			
			// TODO include any dependencies here
			
			
			// now output our local macros
			cFile.println("\r\n\r\n// ******** local macro declarations ********");
			for( BodyDeclaration currBd : (List<BodyDeclaration>)tdIn.bodyDeclarations() )
			{
				// only look for member variables
				if( currBd instanceof FieldDeclaration )
				{
					FieldDeclaration currFd = (FieldDeclaration)currBd;
					if( (((currFd.getModifiers() & Modifier.STATIC) != 0) && ((currFd.getModifiers() & Modifier.FINAL) != 0)) &&
						((currFd.getModifiers() & Modifier.PRIVATE) != 0) )
					{
						// we have a "public|protected static final" variable...render as macro
						cFile.println(renderMacro(currFd));
					}
					
				}
			}
			
			
			// now output our type declarations
			cFile.println("\r\n\r\n// ******** local type declarations ********");
			
			
			// now output our local variable declarations
			cFile.println("\r\n\r\n// ******** local variable declaratations ********");
			
			
			// now our local function prototypes (private functions)
			cFile.println("\r\n\r\n// ******** local function prototypes ********");
			for( BodyDeclaration currBd: (List<BodyDeclaration>)tdIn.bodyDeclarations() )
			{
				// only look for method declarations
				if( currBd instanceof MethodDeclaration )
				{
					MethodDeclaration currMd = (MethodDeclaration)currBd;
					
					// render everything but private
					if( (currMd.getModifiers() & Modifier.PRIVATE) != 0 )
					{
						cFile.printf("%s;\r\n", renderMethodPrototype(currMd, namespace, className));	
					}
				}
			}
			
			
			// now output our member function implementations (everything but private)
			cFile.println("\r\n\r\n// ******** global function implementations ********");
			boolean hasGlobalFunctions = false;
			for( BodyDeclaration currBd: (List<BodyDeclaration>)tdIn.bodyDeclarations() )
			{
				// only look for method declarations
				if( currBd instanceof MethodDeclaration )
				{
					MethodDeclaration currMd = (MethodDeclaration)currBd;
					
					// render everything but private
					if( (currMd.getModifiers() & Modifier.PRIVATE) == 0 )
					{
						hasGlobalFunctions = true;
						cFile.printf("%s\r\n\r\n\r\n", renderMethodImplementation(currMd, namespace, className));	
					}
				}
			}
			if( !hasGlobalFunctions ) cFile.println("\r\n");
				

			// now output our local function implementations (private functions)
			cFile.println("// ******** local function implementations ********");
			for( BodyDeclaration currBd: (List<BodyDeclaration>)tdIn.bodyDeclarations() )
			{
				// only look for method declarations
				if( currBd instanceof MethodDeclaration )
				{
					MethodDeclaration currMd = (MethodDeclaration)currBd;
					
					// render only private functions
					if( (currMd.getModifiers() & Modifier.PRIVATE) != 0 )
					{
						cFile.printf("%s\r\n\r\n\r\n", renderMethodImplementation(currMd, namespace, className));	
					}
				}
			}
		

			cFile.close();
		}
		catch(Exception e)
		{
			System.err.printf("While generating implementation file: '%s'\r\n", e.getMessage());
			return false;
		}
		
		
		return true;
	}
	
	
	private static String getNamespace(CompilationUnit cuIn)
	{
		return cuIn.getPackage().getName().getFullyQualifiedName().replace('.', '_');
	}
	
	
	private static String getClassName(TypeDeclaration tdIn)
	{	
		return Introspector.decapitalize(tdIn.getName().getFullyQualifiedName());
	}
	
	
	private static String renderMacro(FieldDeclaration fdIn) throws ParseException
	{		
		// now determine our variable name
		String variableName = ((VariableDeclarationFragment)fdIn.fragments().get(0)).getName().getFullyQualifiedName();
		
		// now determine our value
		String valueString = "";
		Expression initExpression = ((VariableDeclarationFragment)fdIn.fragments().get(0)).getInitializer();
		if( initExpression instanceof NumberLiteral )
		{
			valueString = ((NumberLiteral)initExpression).getToken();
		}
		else if( initExpression instanceof StringLiteral )
		{
			valueString = ((StringLiteral)initExpression).getEscapedValue();
		}
		else throw new ParseException("Unknown macro type", fdIn);
		
		// now render the return value
		return String.format("#define %-40s %s", variableName, valueString);
	}
	
	
	private static String renderType(Type typeIn) throws ParseException
	{
		if( typeIn == null ) return null;
		
		String typeString = "";
		if( typeIn.isPrimitiveType() )
		{
			// need to render type using primitive type mappings
			PrimitiveType pType = (PrimitiveType)typeIn;
			typeString = primitiveTypeMap.get(pType.getPrimitiveTypeCode());
		}
		else if( typeIn.isSimpleType() )
		{
			// simple type is any "simple" class...no parameterization / genericification / arraying
			SimpleType sType = (SimpleType)typeIn;
			
			// see if we should do any conversion for built-in types
			if( (typeString = builtInTypeMap.get(sType.getName().getFullyQualifiedName())) == null )
			{
				// did not find any matching built-in type...
				throw new ParseException(String.format("No matchingType: '" + sType.getName().getFullyQualifiedName() + "'"), typeIn);
			}
		}
		
		return typeString;
	}
	
	
	private static String renderVariableDeclaration(FieldDeclaration fdIn) throws ParseException
	{	
		// determine our type
		String typeString = renderType(fdIn.getType());
		if( typeString == null ) return null;
		
		// now determine our variable name
		String variableName = ((VariableDeclarationFragment)fdIn.fragments().get(0)).getName().getFullyQualifiedName();
		
		// now render the return value
		return String.format("%s %s", typeString, variableName);
	}
	
	
	private static String renderVariableDeclaration(SingleVariableDeclaration svdIn) throws ParseException
	{
		// determine our type
		String typeString = renderType(svdIn.getType());
		if( typeString == null ) return null;
		
		// now determine our variable name
		String variableName = svdIn.getName().getFullyQualifiedName();
		
		// now render the return value
		return String.format("%s %s", typeString, variableName);
	}
	
	
	private static String renderMethodPrototype(MethodDeclaration mdIn, String namespaceIn, String classNameIn) throws ParseException
	{
		// see if we are private
		String staticStr = ( (mdIn.getModifiers() & Modifier.PRIVATE) != 0 ) ? "static " : "";
		
		// determine our return type
		String retTypeString = mdIn.isConstructor() ? "void" : renderType(mdIn.getReturnType2());
		if( retTypeString == null )
		{
			// no return type...check to see if this is a constructor...
			System.out.printf("%s %s\r\n", mdIn.getName().getFullyQualifiedName(), classNameIn);
			throw new ParseException("Error determining method return type", mdIn);
		}
		
		// now our variable types (common to both constructor and methods)
		String variableList = "";
		List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>)mdIn.parameters();
		for( int i = 0; i < parameters.size(); i++ )
		{
			SingleVariableDeclaration currSd = parameters.get(i);
			
			variableList += renderVariableDeclaration(currSd);
			
			if( i < (parameters.size()-1) )
			{
				variableList += ", ";
			}
		}
		if( parameters.size() == 0 ) variableList = "void";
		
		// now render our method name differently if we are constructor
		String methodName = mdIn.getName().getFullyQualifiedName();
		if( mdIn.isConstructor() )
		{
			// we are a constructor...
			ArrayList<MethodDeclaration> constructorList = getCuConstructorList(mdIn);
			
			// see if we've been rendered before (and have an already-assigned index)
			int constructorIndex = constructorList.indexOf(mdIn);
			if( constructorIndex == -1 )
			{
				// this is a newly encountered constructor
				constructorIndex = constructorList.size();
				constructorList.add(mdIn);
			}
			
			// now render the method name
			methodName = String.format("constructor%d", constructorIndex);
		}
		
		return String.format("%s%s %s_%s_%s( %s )", staticStr, retTypeString, namespaceIn, classNameIn, methodName, variableList);
	}
	
	
	private static String renderMethodImplementation(MethodDeclaration mdIn, String namespaceIn, String classNameIn) throws ParseException
	{
		
		return String.format("%s\r\n{\r\n}", renderMethodPrototype(mdIn, namespaceIn, classNameIn));
	}
	
	
	private static ArrayList<MethodDeclaration> getCuConstructorList(ASTNode nodeIn)
	{
		ArrayList<MethodDeclaration> retVal = (ArrayList<MethodDeclaration>)((CompilationUnit)nodeIn.getRoot()).getProperty(CU_PROPERTY_CONSTRUCTOR_LIST);
		
		// see if we need to initialize the constructor list
		if( retVal == null )
		{
			retVal = new ArrayList<MethodDeclaration>();
			((CompilationUnit)nodeIn.getRoot()).setProperty(CU_PROPERTY_CONSTRUCTOR_LIST, retVal);
		}
		
		return (retVal == null) ? (new ArrayList<MethodDeclaration>()) : retVal; 
	}
}