package grader;

import java.io.File;

public abstract class Update 
{
	public abstract boolean isApplicable(String path);
	public abstract void apply(File targetFile, String packageName);
}
