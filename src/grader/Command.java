package grader;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class Command 
{
	private String executable;
	private String arguments;
	private String output;
	private String workdir;
	private boolean redirectOutput;
	
	public Command(String exe, String args, String output, String workdir)
	{
		if (exe == null || exe.trim().equals(""))
			throw new IllegalArgumentException("Each Command must include an Executable.");
		this.executable = exe;
		
		if (args == null || args.trim().equals(""))
			this.arguments = "";
		else
			this.arguments = args;

		if (output == null || output.trim().equals(""))
		{
			this.redirectOutput = false;
		}
		else
		{
			this.redirectOutput = true;
			this.output = output;
		}
		
		if (workdir == null || workdir.trim().equals(""))
			this.workdir = ".";
		else
			this.workdir = workdir;
	
	}

	public void execute(Function<String, String> fixer)
	{
		try
		{
			ArrayList<String> args = new ArrayList<String>();
			args.add(fixer.apply(executable));
			args.addAll(Arrays.asList(fixer.apply(arguments).split("\\s+")));
			ProcessBuilder builder = new ProcessBuilder(args);
			builder = builder.directory(new File(fixer.apply(workdir)));
			
			if (this.redirectOutput)
			{
				File outFile = new File(fixer.apply(this.output));
				builder = builder.redirectOutput(Redirect.to(outFile));
				builder = builder.redirectErrorStream(true);
			}
			
			Process cmd = builder.start();
			cmd.waitFor();
		}
		catch (Exception ex)
		{
			// TODO: write to error file
			System.out.println("failed to run command\n\tcmd: " + executable + "\n\targs: " + arguments + "\n\tworkdir: " + workdir);
			ex.printStackTrace();
		}
	}
	
	@Override
	public String toString()
	{
		return this.executable + " " + this.arguments;
	}

}
