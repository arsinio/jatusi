package jatusi.test;

/**
 * @author Christopher Armenio
 *
 */
public class JatusiTest
{	
	public static final short CONST_PUBLIC_SHORT = 1;
	protected static final double CONST_PROTECTED_DOUBLE = 1.0;
	private static final int CONST_PRIVATE_INT = 1;
	private static final String CONST_PRIVATE_STRING = "test string\r\n";
	
	private static short static_short;
	
	int member_default_int = 1;
	public double member_public_double;
	protected int member_protected_int;
	private short member_private_short;
	
	
	JatusiTest()
	{	
	}
	
	public JatusiTest(int arg0)
	{	
	}
	
	
	protected JatusiTest( int arg0, short arg1 )
	{
	}
	
	
	private JatusiTest( int arg0, short arg1, double arg2 )
	{
	}
	
	
	void defaultMethod0(int arg0, double arg1, short arg2)
	{
	}
	
	
	public void publicMethod0(int arg0, double arg1, short arg2)
	{
	}
	
	
	protected void protectedMethod0(int arg0, double arg1, short arg2)
	{
	}
	
	
	private void privateMethod0(int arg0, double arg1, short arg2)
	{
	}
}
