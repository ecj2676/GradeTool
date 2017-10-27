package grader;

import java.io.File;

public class RenameFile extends Update
{
	private String oldName;
	private String newName;
	private boolean overwriteExisting;
	
	public RenameFile(String comment, String oldName, String newName, boolean overwrite)
	{
		this.oldName = oldName;
		this.newName = newName;
		this.overwriteExisting = overwrite;
	}
	
	@Override
	public boolean isApplicable(String path)
	{
		return path.endsWith(this.oldName);
	}
	
	@Override
	public void apply(File targetFile, String packageName)
	{
		File newPath = new File(targetFile.getParent() + File.separator + this.newName);
		if (!newPath.exists() || overwriteExisting)
			targetFile.renameTo(newPath);
	}

}
