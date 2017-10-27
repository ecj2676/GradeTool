package grader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class PackageNameUpdate extends ReplaceInFile 
{
	public PackageNameUpdate()
	{
		super("package rename", "(\\A|\\n)package\\s+([^;]+);", 2, "{PACKAGE}", "\\.java");
	}
	
	@Override
	public void apply(File targetFile, String packageName)
	{
		try
		{
			String fileContents = String.join("\n", Files.readAllLines(targetFile.toPath()));
			String newText = replaceText(packageName, fileContents);
			
			// if there was no package declaration to replace, insert a package statement
			// at the top of the file
			if (!newText.contains("package " + packageName + ";"))
				newText = "package " + packageName + ";\n\n" + newText;

			Files.write(targetFile.toPath(), newText.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
		catch (IOException ex)
		{
			System.out.println("Unable to apply ReplaceInFile update to " + targetFile.getName() + " in package " + packageName);
		}
	}


}
