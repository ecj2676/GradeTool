package grader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceInFile extends Update
{
	private String name;
	private Pattern pattern;
	private String replacement;
	private String fileFilter;
	
	public ReplaceInFile(String name, String pattern, String replacement, String fileFilter)
	{
		this.name = name;
		this.pattern = Pattern.compile(pattern);
		this.replacement = replacement;
		this.fileFilter = fileFilter;
	}
	
	@Override
	public String toString() { return this.name; }
	
	public boolean isApplicable(String path)
	{
		return path.matches(".*" + fileFilter + ".*") || path.toLowerCase().matches(".*" + fileFilter + ".*");
	}
	
	public void apply(File targetFile, String packageName)
	{
		try
		{
			String fileContents = String.join("\n", Files.readAllLines(targetFile.toPath()));
			String newText = replaceText(packageName, fileContents);
			
			Files.write(targetFile.toPath(), newText.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
		catch (IOException ex)
		{
			System.out.println("Unable to apply ReplaceInFile update to " + targetFile.getName() + " in package " + packageName);
		}
	}

	protected String replaceText(String packageName, String fileContents) 
	{
		Matcher matcher = this.pattern.matcher(fileContents);
		String updatedReplacement = this.replacement.replace("{PACKAGE}", packageName);
		if (matcher.find())
		{
			// allow users to grab chunks of the matching text and use them in the replacement
			// by specifying syntax like {n} where n is the group number
			for (int g = 0; g < matcher.groupCount(); g++)
			{
				String search = "{" + g + "}";
				if (updatedReplacement.contains(search))
				{
					String r = matcher.group(g);
					updatedReplacement = updatedReplacement.replace(search, r);
				}
			}
		}
		
		return matcher.replaceAll(updatedReplacement);
	}

}
