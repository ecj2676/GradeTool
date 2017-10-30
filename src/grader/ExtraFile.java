package grader;

import java.io.File;

/* 
 * ExtraFile is a utility class to encapsulate an ExtraFile element
 * from the input options. It is basically just a path to a file, but
 * can optionally have a different name so that when a file is copied
 * from one location to another, it can be renamed as part of the copy.
 */
public class ExtraFile 
{
	private String path;
	private String newName;
	
	public ExtraFile(String path)
	{
		this.path = path;
		this.newName = getName();
	}
	
	public String getPath() { return this.path; }
	public String getNewName() { return this.newName; }
	public void setNewName(String newName) { this.newName = newName; }
	public String getName() { return path.substring(path.lastIndexOf(File.separatorChar) + 1); }
}
