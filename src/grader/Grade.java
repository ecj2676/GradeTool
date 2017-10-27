package grader;

/*
 * Grade is the main entry point for the program. It primarily uses
 * the PackageManager class to create packages and operate on them.
 * 
 * Ideas that I haven't implemented yet:
 *   - measure code coverage. This would just be a new command, not a
 *     code change. There does seem to be a way to run emma from the
 *     command line (java -cp whatever emmarun ...)
 *   - include a file in the output with links to the moodle grading
 *     page and/or phabricator comment pages. For moodle grading, this
 *     probably isn't needed. For phab, it's tricky because the URLs 
 *     use the short name for each project but the downloaded code 
 *     only has the long name (AlGhamdi Elevens vs JELEV)
 *   - add a mode that downloads from git directly instead of pulling
 *     from a local directory structure. There are git APIs for this.
 *   - Refactor package manager to be less monolithic and easier to
 *     test
 */
public class Grade 
{
	public static void main(String[] args) 
	{
		GradeOptions options = new GradeOptions(args);

		PackageManager manager = new PackageManager(options);
		manager.createPackagesFromSource();
		
		for (ExtraFile extraFile : options.getAdditionalFiles())
			manager.addFileToPackages(extraFile);

		manager.apply(new PackageNameUpdate());
		for (Update update : options.getUpdates())
			manager.apply(update);

		for (Command command : options.getCommands())
			manager.run(command);
		
		System.out.println("Grading run complete.");
	}

}
