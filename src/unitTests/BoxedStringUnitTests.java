package unitTests;

import java.io.File;
import java.io.FilenameFilter;

import org.junit.BeforeClass;
import org.junit.Test;

import testHelp.ConsoleTester;
import testHelp.verify;

public class BoxedStringUnitTests
{
	private static String className;
	
	@BeforeClass
	public static void findClassName()
	{
		class JavaFilter implements FilenameFilter
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".java") && !name.contains("BoxedStringUnitTests");
			}
		}
		
		String packageName = "{PACKAGE_NAME}"; // will replace in Grade config
		File currentDir = new File("./src/" + packageName).getAbsoluteFile();
		String fileName = currentDir.list(new JavaFilter())[0];
		className = packageName + "." + fileName.substring(0, fileName.length() - 5); // remove .java
	}
	
	@Test
	public void printsPrompt()
	{
		String output = ConsoleTester.getOutput(className, "boxed string\r\nquit");
		verify.that(output).matches("\\AEnter a string \\(or \"quit\"\\): ");
	}
	
	@Test
	public void printsBoxedString()
	{
		String output = ConsoleTester.getOutput(className, "This is a boxed string\r\nquit");
		verify.that(output).matches("---------------------------------------------[\r\n]+\\|T\\|h\\|i\\|s\\| \\|i\\|s\\| \\|a\\| \\|b\\|o\\|x\\|e\\|d\\| \\|s\\|t\\|r\\|i\\|n\\|g\\|[\r\n]+---------------------------------------------");
	}
	
	@Test
	public void handlesMultipleInputs()
	{
		String first = "first", second = "second";
		String output = ConsoleTester.getOutput(className, first + "\r\n" + second + "\r\nquit");
		verify.that(output).matches(makeBoxRegex(first));
		verify.that(output).matches(makeBoxRegex(second));
	}
	
	private static String makeBoxRegex(String input)
	{
		StringBuilder output = new StringBuilder();
		
		for (int i = 0; i < 2 * input.length() + 1; i++)
			output.append("-");
		output.append("[\\r\\n]+");
		
		for (int i = 0; i < input.length(); i++)
			output.append("\\|" + input.charAt(i));
		output.append("\\|[\\r\\n]+");
		
		for (int i = 0; i < 2 * input.length() + 1; i++)
			output.append("|");
		output.append("[\\r\\n]+");
		
		return output.toString();
	}
}
