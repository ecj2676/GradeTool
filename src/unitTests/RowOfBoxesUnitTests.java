package unitTests;

import org.junit.Test;
import testHelp.*;

public class RowOfBoxesUnitTests
{
	@Test
	public void promptsForBoxesFirstTime()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "7\r\n10\r\n0\r\n");
		verify.that(response).matches("\\AHow many boxes\\? ");
	}

	@Test
	public void promptsForSizeFirstTime()
	{
		// only inputting one set of numbers so the size prompt should be the first time around
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "7\r\n10\r\n0\r\n");
		verify.that(response).matches("\\w+\\?(\\s|\\r|\\n)+What size\\? ");
	}

	@Test
	public void promptsForBoxesSecondTime()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "7\r\n10\r\n3\r\n5\r\n0\r\n");
		verify.that(response).matches("[\\w\\W\\n]{5,}How many boxes\\? ");
	}

	@Test
	public void promptsForSizeSecondTime()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "7\r\n10\r\n3\r\n5\r\n0\r\n");
		verify.that(response).matches("[\\w\\W\\n]{20,}What size\\? ");
	}
	
	@Test
	public void quitsAfterBoxes()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "7\r\n10\r\n0\r\n");
		verify.that(response).matches("[\\w\\W\\n]{20,}All done!");
	}

	@Test
	public void quitsBeforeBoxes()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "0\r\n");
		verify.that(response).matches("\\w+\\?(\\s|\\r|\\n)+All done!");
	}

	@Test
	public void prints7BoxesOf10()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "7\r\n10\r\n0\r\n");
		verify.that(response).matches(makeBoxRegex(7, 10));
	}
	
	@Test 
	public void prints3BoxesOf5()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "3\r\n5\r\n0\r\n");
		verify.that(response).matches(makeBoxRegex(3, 5));
	}
	
	@Test 
	public void prints1BoxOf8()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "1\r\n8\r\n0\r\n");
		verify.that(response).matches(makeBoxRegex(1, 8));
	}
	
	@Test 
	public void prints4BoxesOf0()
	{
		String response = ConsoleTester.getOutput("rowOfBoxes.RowOfBoxes", "4\r\n0\r\n0\r\n");
		verify.that(response).matches(makeBoxRegex(4, 0));
	}
	
	private String makeBoxRegex(int numBoxes, int boxSize)
	{
		return String.format("(O\\={%2$d}){%1$d}O[\\s\\r\\n]+(\\| {%2$d}){%1$d}\\|[\\s\\r\\n]+(O\\={%2$d}){%1$d}O", numBoxes, boxSize);
	}
}
