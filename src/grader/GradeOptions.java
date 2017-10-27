package grader;

import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.*;

/*
 * GradeOptions can translate command-line arguments or an XML
 * options file into the information the Grade app needs to do 
 * a grading run.
 */
public class GradeOptions 
{
	// getting files from a moodle download versus a phabricator
	// download is handled differently, so we have an enum to 
	// keep track of which we're doing.
	public enum SourceType
	{
		moodle,
		phabricator
	}

	private String eclipseProjectRoot;
	private String sourceRoot;
	private SourceType sourceType = SourceType.moodle;
	private ArrayList<ExtraFile> extraFiles;
	private boolean clearTargetProject;
	private ArrayList<Update> updates;
	private ArrayList<String> sourceFilters;
	private ArrayList<Command> commands;
	private boolean copyToEclipse;
	
	/**
	 * Create a new GradeOptions object based on command-line arguments
	 * passed to a program's main() method.
	 * 
	 * @param args
	 */
	public GradeOptions(String[] args)
	{
		extraFiles = new ArrayList<ExtraFile>();
		updates = new ArrayList<Update>();
		sourceFilters = new ArrayList<String>();
		commands = new ArrayList<Command>();
		
		for (String arg : args)
		{
			String name = arg.substring(1, arg.indexOf(":"));
			String value = arg.substring(arg.indexOf(":") + 1);
			
			if (name.equals("eclipseProjectRoot"))
				eclipseProjectRoot = value;
			else if (name.equals("sourceRoot"))
				sourceRoot = value;
			else if (name.equals("sourceType"))
				sourceType= SourceType.valueOf(value.toLowerCase());
			else if (name.equals("extraFile"))
				extraFiles.add(new ExtraFile(value));
			else if (name.equals("optionFile"))
				loadOptions(value);
			else
				throw new IllegalArgumentException("'" + arg +"' is not a valid argument");
		}

		checkRequiredArgument(sourceRoot, "Source root must be specified with /sourceRoot:<path to source root>");
		checkRequiredArgument(eclipseProjectRoot, "Eclipse project root must be specified with /eclipseProjectRoot:<path to eclipse project root>");
	}

	/**
	 * EclipseProjectRoot describes the path to an Eclipse project where 
	 * this app will create packages with student code for grading. Specify 
	 * as /eclipseProjectRoot:<path> on the command line or as the value for
	 * an EclipseProjectRoot element in an input XML file.
	 * 
	 * @return path to root of an eclipse project
	 */
	public String getEclipseProjectRoot() { return eclipseProjectRoot; }
	
	/**
	 * SourceRoot describes the path where student files are available. It 
	 * can be a set of files downloaded from Moodle or a set of projects
	 * cloned from git, e.g. using Bannus's getRepos.py script. Specify /sourceRoot
	 * at the command line or as the path attribute to a SourceRoot element
	 * in an input XML file.
	 *  
	 * @return root path where student source code can be found
	 */
	public String getSourceRoot() { return sourceRoot; }
	
	/**
	 * SourceType describes what kind of structure to expect at SourceRoot. It 
	 * defaults to moodle but can also be phabricator. 'moodle' indicates a 
	 * flat structure where student names are included in file names. 
	 * 'phabricator' indicates a cloned directory structure from git where 
	 * the first level of directories is projects. Specified as /sourceType on
	 * the command line or as a sourceType attribute to the SourceRoot element
	 * in the input XML file.
	 * 
	 * @return a value indicating the structure of the files at source root
	 */
	public SourceType getSourceType() { return sourceType; }
	
	public boolean copyToEclipse() { return copyToEclipse; }
	
	/**
	 * Additional files that need to be copied into each package. Specify
	 * on the command line with /extraFile or in the XML with an ExtraFile
	 * element. Via XML you can also provide a rename attribute that will
	 * allow files to be renamed when they are copied. You can use {PACKAGE},
	 * {ECLIPSE_PROJECT_ROOT}, and {SOURCE_ROOT} as placeholders in the 
	 * files' paths.
	 * 
	 * @return a collection of files to be copied into each package.
	 */
	public ArrayList<ExtraFile> getAdditionalFiles() { return extraFiles; }
	
	/**
	 * Whether to clear the files from the Eclipse project before starting this
	 * grading run. If false, files will be overwritten but old files that aren't
	 * overwritten won't be deleted. Unless you have a reason to keep old files
	 * around, it's probably best to set this to true.
	 * 
	 * @return a value indicating whether to clear files in the Eclipse project
	 */
	public boolean shouldClearTargetProject() { return clearTargetProject; }
	
	/**
	 * Gets a list of updates that should be applied to files in student packages,
	 * e.g. text replacement to be done.
	 * 
	 * @return updates to be applied to each file
	 */
	public ArrayList<Update> getUpdates() { return updates; }

	/**
	 * Gets a list of commands to run after the packages have been set up
	 * 
	 * @return commands to be run
	 */
	public ArrayList<Command> getCommands() { return commands; }

	/** 
	 * Get a list of patterns we can use to filter source files when copying from
	 * moodle or phabricator. Only files that match at least one pattern will be
	 * included in the generated Eclipse packages.
	 * 
	 * @return the list of filters to apply
	 */
	public ArrayList<String> getSourceFilters() 
	{
		return sourceFilters; 
	}
	
	// verify that an argument was supplied if it was required
	private void checkRequiredArgument(String arg, String message)
	{
		if (arg == null || arg.trim().equals(""))
		throw new IllegalArgumentException(message);
	}
	
	// load from the XML file
	private void loadOptions(String optionFilePath)
	{
		try
		{
			// set up an xpath object to pull things out of the XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(optionFilePath);
			XPath xpath = XPathFactory.newInstance().newXPath();

			// grab basic options
			sourceRoot = xpath.evaluate("/GradingRun/SourceRoot/@path", doc).trim();
			String sourceTypeString = xpath.evaluate("/GradingRun/SourceRoot/@type", doc).trim().toLowerCase();
			if (!sourceTypeString.isEmpty())
				sourceType = SourceType.valueOf(sourceTypeString);
			eclipseProjectRoot = xpath.evaluate("/GradingRun/EclipseProjectRoot", doc).trim();
			String clear = xpath.evaluate("/GradingRun/EclipseProjectRoot/@clear", doc).trim();
			clearTargetProject = clear.equalsIgnoreCase("true");
			String copyFiles = xpath.evaluate("/GradingRun/SourceRoot/@copyToEclipse", doc);
			copyToEclipse = copyFiles == null || !copyFiles.equalsIgnoreCase("false");
		
			// get lists of things
			addSourceFilters(doc, xpath);
			addExtraFiles(doc, xpath);
			addUpdates(doc, xpath);
			addCommands(doc, xpath);
		}
		catch (Exception ex)
		{
			System.out.println("Error loading option file '" + optionFilePath + "'");
			ex.printStackTrace();
		}
	}

	private void addExtraFiles(Document doc, XPath xpath) 
	{
		for (Element e : getElements(doc, xpath, "/GradingRun/ExtraFiles/File"))
		{
			ExtraFile ef = new ExtraFile(e.getTextContent().trim());
			if (e.hasAttribute("rename"))
				ef.setNewName(e.getAttribute("rename"));
			extraFiles.add(ef);
		}
	}

	private void addUpdates(Document doc, XPath xpath) 
	{
		for (Element e : getElements(doc, xpath, "/GradingRun/Updates/*"))
		{
			if (e.getNodeName().equalsIgnoreCase("replace"))
			{
				Update u = new ReplaceInFile(
						e.getAttribute("name"),
						e.getAttribute("pattern"), 
						e.hasAttribute("group") ? Integer.parseInt(e.getAttribute("group")) : 0,
						e.getAttribute("replacement"), 
						e.getAttribute("fileFilter"));
				updates.add(u);
			}
			else if (e.getNodeName().equalsIgnoreCase("rename"))
			{
				Update u = new RenameFile(
						e.getAttribute("name"),
						e.getAttribute("oldName"),
						e.getAttribute("newName"),
						e.getAttribute("overwriteExisting").equalsIgnoreCase("true"));
				updates.add(u);
			}
			else
			{
				System.out.println("Unrecognized update in options file: " + e.getNodeName());
			}
		}
	}

	private void addCommands(Document doc, XPath xpath) 
	{
		for (Element e : getElements(doc, xpath, "/GradingRun/Commands/Command")) try
		{
			String exe = xpath.evaluate("Executable", e);
			String args = xpath.evaluate("Arguments", e);
			String output = xpath.evaluate("Output", e);
			String workdir = xpath.evaluate("WorkingDirectory", e);
			commands.add(new Command(exe, args, output, workdir));
			
		}
		catch (XPathExpressionException ex)
		{
			ex.printStackTrace();
		}
	}

	private void addSourceFilters(Document doc, XPath xpath) 
	{
		for (Element e : getElements(doc, xpath, "/GradingRun/SourceRoot/Filters/*"))
			sourceFilters.add(e.getAttribute("pattern"));

		if (sourceFilters.isEmpty())
			sourceFilters.add(".*");
	}

	private ArrayList<Element> getElements(Document doc, XPath xpath, String path)
	{
		ArrayList<Element> result = new ArrayList<Element>();
		
		try
		{
			NodeList extraFileElements = (NodeList)xpath.evaluate(path, doc, XPathConstants.NODESET);
			for (int i = 0; i < extraFileElements.getLength(); i++)
			{
				result.add((Element)extraFileElements.item(i));
			}			
		}
		catch (XPathExpressionException ex)
		{
			// it's perfectly fine for there to be no files to copy
		}
		
		return result;
	}
}
