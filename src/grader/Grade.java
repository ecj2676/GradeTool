package grader;

/*
 * Grade is the main entry point for the program. It primarily uses
 * the PackageManager class to create packages and operate on them.
 * 
 * I typically have a project called Submissions set up in my Eclipse
 * workspace. I usually have testhelp.jar and JUnit4 already added to
 * it. I use this as the EclipseProjectRoot in an XML options file.
 * 
 * I start grading an assignment by bulk downloading submissions as
 * a zip file from moodle and extracting them to a temp directory
 * somewhere.
 * 
 * Then I run Grade with a configuration that adds /optionFile:<path 
 * to options file> as a parameter. In the options file, I've added
 * the temp path where I downloaded moodle files as the SourceRoot 
 * path.
 * 
 * I will often add an ExtraFiles element and use it to copy a set
 * of unit tests into each student's package. Even if the assignment 
 * didn't include UT, I usually write a quick set to do some functional
 * verification so I don't have to run everyone's code manually.
 * 
 * Running Grade with an options file like this will fill in your
 * Submissions project with a package per student. Each package will 
 * have the student's submitted code and whatever UT you wanted to
 * add.
 * 
 * You can do some fixup on files, like basic text substitutions, and
 * you can run arbitrary commands. I've used this sometimes to automatically
 * run JUnit tests and generate an output file that gets added to each
 * student's package. Getting all the paths and parameters right is
 * usually more work than it's worth.
 */
public class Grade 
{
	public static void main(String[] args) 
	{
		GradeOptions options = new GradeOptions(args);

		// get the files the student submitted into package directories
		PackageManager manager = new PackageManager(options);
		manager.createPackagesFromSource();
		
		// add any extra files to each package directory
		for (ExtraFile extraFile : options.getAdditionalFiles())
			manager.addFileToPackages(extraFile);

		// fix up the package declaration in all the files, and run
		// any other arbitrary updates that were requested.
		manager.apply(new PackageNameUpdate());
		for (Update update : options.getUpdates())
			manager.apply(update);

		// run any arbitrary commands that were added
		for (Command command : options.getCommands())
			manager.run(command);
		
		System.out.println("Grading run complete.");
	}

}
