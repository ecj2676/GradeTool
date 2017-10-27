package grader;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PackageManager 
{
	private GradeOptions options;
	private HashSet<String> packages;
	
	public PackageManager(GradeOptions options)
	{
		this.options = options;
		this.packages = new HashSet<String>();
	}

	public void createPackagesFromSource()
	{
		clearTargetProjectIfRequested();

		if (options.getSourceType() == GradeOptions.SourceType.moodle)
		{
			System.out.println("creating packages from moodle source at '" + options.getSourceRoot() + "'");
			createMoodlePackages();
		}
		else
		{
			System.out.println("pulling files from phabricator download at '" + options.getSourceRoot() + "'");
			createGitPackages();
		}
	}
	
	public void addFileToPackages(ExtraFile file)
	{
		System.out.println("Copying '" + file.getName() + "' to all packages.");
		for (String packageName : packages) try
		{
			File source = new File(fixPath(file.getPath(), packageName));
			Path target = Paths.get(options.getEclipseProjectRoot(), 
					"src", packageName, file.getNewName());
			Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException ex)
		{
			System.out.println("Error copying '" + fixPath(file.getPath(), packageName) + "' to package " + packageName + "\n" + ex);
		}
	}

	public void apply(Update update)
	{
		for (String packageName : packages)
		{
			File packageDir = new File(options.getEclipseProjectRoot() + "\\src\\" + packageName);
			for (File f : packageDir.listFiles())
			{
				if (!update.isApplicable(f.getAbsolutePath()))
					continue;
				
				update.apply(f, packageName);
			}
		}
	}

	public void run(Command command)
	{
		System.out.println("Running command in all packages: " + command);
		for (String packageName : this.packages)
		{
			command.execute((String str) -> this.fixPath(str,  packageName));
		}
	}
	
	/* pre: have a source root with files in moodle format
	 * post: package dir was created for each package and each file was renamed and copied into the dir
	 */
	private void createMoodlePackages()
	{
		File root = new File(options.getSourceRoot());
		if (!root.isDirectory())
			throw new IllegalArgumentException("Source root must be a non-empty directory ('" + options.getSourceRoot() + "' is invalid)");

		File[] files = root.listFiles();
		if (files.length == 0)
			throw new IllegalArgumentException("Source root must be a non-empty directory ('" + options.getSourceRoot() + "' is invalid)");

		for (File file : files)
		{
			String[] parts = file.getName().split("_");
			String packageName = parts[0].replace(' ', '_').replace('-', '_');
			String newFileName = parts[1] + ".java";

			packages.add(packageName);
			
			File packageDir = new File(options.getEclipseProjectRoot() + "\\src\\" + packageName);
			if (!packageDir.exists())
				packageDir.mkdir();
			
			try
			{
				Files.copy(file.toPath(), Paths.get(packageDir.getAbsolutePath(), newFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			catch (IOException ex)
			{
				System.out.println("Error copying " + file.getAbsolutePath() + "\n" + ex);
			}
		}
	}
	
	public void createGitPackages()
	{
		// assume the repositories have been cloned locally--would be nice to
		// pull directly from git but not going to spend the time to figure
		// that out
		
		// for each project
		//   for each file pattern from options
		//     for each file that matches the pattern
		//       if this is the first file from this project
		//         create a package name and dir based on the project name
		//       add the file to the package
		
		File projectRoot = new File(options.getSourceRoot());
		for (File projectDir : projectRoot.listFiles())
		{
			if (!projectDir.isDirectory())
				continue;
			
			createPackageFromGitProject(projectDir);
		}
	}
	
	private void createPackageFromGitProject(File projectDir)
	{
		ArrayList<File> filesToInclude = new ArrayList<File>();
		for (String filePattern : options.getSourceFilters())
			findFilesMatchingPattern(projectDir, ".*" + filePattern + ".*", filesToInclude);

		if (filesToInclude.size() == 0)
			return; // this project didn't have any of the files we wanted

		String packageName = makeLegal(projectDir.getName());
		packages.add(packageName);
		
		File packageDir = new File(options.getEclipseProjectRoot() + "\\src\\" + packageName);
		if (!packageDir.exists())
			packageDir.mkdir();

		if (!options.copyToEclipse())
			return;
		
		try
		{
			for (File f : filesToInclude)
				Files.copy(f.toPath(), Paths.get(packageDir.getAbsolutePath(), f.getName()), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException ex)
		{
			System.out.println("Error copying file from " + projectDir.getAbsolutePath() + "\n" + ex);
		}
	}
	
	private void findFilesMatchingPattern(File dir, String pattern, ArrayList<File> result)
	{
		if (!dir.isDirectory())
			return;
		
		for (File child : dir.listFiles())
		{
			if (child.isDirectory())
			{
				findFilesMatchingPattern(child, pattern, result);
			}
			else 
			{
				String path = child.getAbsolutePath();
				if (path.matches(pattern) || path.toLowerCase().matches(pattern))
					result.add(child);
			}
		}
	}
	
	private void clearTargetProjectIfRequested()
	{
		if (!options.shouldClearTargetProject())
			return;
		
		File src = new File(options.getEclipseProjectRoot() + "\\src");
		deletePath(src);
		src.mkdir();
	}
	
	private void deletePath(File target)
	{
		if (!target.exists())
			return;
		
		if (target.isDirectory())
		{
			for (File f : target.listFiles())
				deletePath(f);
		}
		
		target.delete();
	}
	
	private String fixPath(String path, String packageName)
	{
		return path
			.replace("{PACKAGE}", packageName)
			.replace("{SOURCE_ROOT}", options.getSourceRoot())
			.replace("{ECLIPSE_PROJECT_ROOT}", options.getEclipseProjectRoot());
	}
	
	private String makeLegal(String input)
	{
		char[] letters = input.toCharArray();
		for (int i = 0; i < letters.length; i++)
		{
			if (!("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxzy0123456789".contains("" + letters[i])))
				letters[i] = '_';
		}
		
		return new String(letters);
	}
}
