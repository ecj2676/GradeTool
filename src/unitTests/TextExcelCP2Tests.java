package unitTests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import testHelp.*;

public class TextExcelCP2Tests
{
	int COLUMN_WIDTH = 13;
	final int ROWS = 11;
	final int COLS = 8;
	
	private String emptyGrid =
			  "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|            \\|     A      \\|     B      \\|     C      \\|     D      \\|     E      \\|     F      \\|     G      \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     1      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     2      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     3      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     4      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     5      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     6      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     7      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     8      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     9      \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n"
			+ "\\|     10     \\|            \\|            \\|            \\|            \\|            \\|            \\|            \\|\r?\n"
			+ "\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+------------\\+\r?\n";

	/*
	 * Checkpoint 1 tests
	 */

	@Test
	public void TextExcelShouldPromptGreetingFirst()
	{
		// NOTE: even though not breaking on invalid input is extra credit, it
		// would be helpful if you program doesn't break when the input is
		// empty, in case you accidentally hit enter when running your program.
		// This test will fail unless you check that the input is not empty.
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "\r\nquit");
		verify.that(response).matches("\\AWelcome to Text Excel!");
	}

	@Test
	public void TextExcelShouldPromptForACommand()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "\r\nquit");
		verify.that(response).matches("Enter a command: ");
	}

	@Test
	public void TextExcelShouldPrintGoodbyeLast()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "quit");
		verify.that(response).matches("Farewell!\\Z");
	}

	@Test
	public void TextExcelShouldPrintGrid()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "print\r\nquit");
		verify.that(response).matches(emptyGrid);
	}

	/*
	 * Checkpoint 2 tests
	 */

	@Test
	public void TextExcelCanInputString()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "A1 = \"Hello!\"\r\nprint\r\nquit");
		verify.that(getCell(response, 'A', 1)).matches("   Hello!   ");
	}

	@Test
	public void TextExcelCanInputDate()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "B8 = 02/06/1976\r\nprint\r\nquit");
		verify.that(getCell(response, 'B', 8)).matches(" 02/06/1976 ");
	}

	@Test
	public void TextExcelCanInputInt()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "D3 = 1729\r\nprint\r\nquit");
		verify.that(getCell(response, 'D', 3)).matches("   1729.0   ");
	}

	@Test
	public void TextExcelCanInputDecimal()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "D3 = 3.14159\r\nprint\r\nquit");
		verify.that(getCell(response, 'D', 3)).matches("  3.14159   ");
	}

	@Test
	public void TextExcelCanInputLongString()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel",
				"A1 = \"Hello! This string is too long.\"\r\nprint\r\nquit");
		verify.that(getCell(response, 'A', 1)).matches("Hello! This>");
	}

	@Test
	public void TextExcelCanInputLongDecimal()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "D3 = 3.141592653589793238\r\nprint\r\nquit");
		verify.that(getCell(response, 'D', 3)).matches("3.141592653>");
	}

	@Test
	public void TextExcelCanPrintString()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel",
				"A1 = \"Hello! This string is too long.\"\r\nA1\r\nquit");
		verify.that(response).matches("A1 = \"Hello! This string is too long.\"");
	}

	@Test
	public void TextExcelCanPrintDate()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "B8 = 02/06/1976\r\nB8\r\nquit");
		verify.that(response).matches("B8 = 02/06/1976");
	}

	@Test
	public void TextExcelCanPrintInt()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "D3 = 1729\r\nD3\r\nquit");
		verify.that(response).matches("D3 = 1729");
	}

	@Test
	public void TextExcelCanPrintDecimal()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "D3 = 3.14159\r\nD3\r\nquit");
		verify.that(response).matches("D3 = 3.14159");
	}

	@Test
	public void TextExcelCanChangeType()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "D3 = 3\r\nD3 = \"hi\"\r\nprint\r\nquit");
		verify.that(getCell(response, 'D', 3)).matches("     hi     ");
	}

	@Test
	public void TextExcelCanContainMultipleValues()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "D3 = 3\r\nD4 = \"hi\"\r\nprint\r\nquit");
		verify.that(getCell(response, 'D', 3)).matches("    3.0     ");
		verify.that(getCell(response, 'D', 4)).matches("     hi     ");
	}

	@Test
	public void TextExcelCanInputToLastCell()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "G10 = \"hi\"\r\nprint\r\nquit");
		verify.that(getCell(response, 'G', 10)).matches("     hi     ");
	}

	@Test
	public void TextExcelEmptyCellisEmpty()
	{
		String response = ConsoleTester.getOutput("textExcel2.TextExcel", "G10 = \"hi\"\r\nF5\r\nquit");
		verify.that(response).matches("F5 = <empty>");
	}

	/*
	 * Private helpers
	 */
	
	private String getCell(String response, char displayCol, int displayRow)
	{
		int linesBeforeTargetLine = displayRow * 2 + 1; // *2 for the separators, +1 for the last separator
		int cellsBeforeTargetCell = displayCol - 'A' + 1;
		String grid = response.substring(response.indexOf("+----"));

		String searchPattern = "\\A(.+\\R+){" + linesBeforeTargetLine + "}.{" + (13 * cellsBeforeTargetCell + 1 ) + "}(.{12})";
		Pattern pattern = Pattern.compile(searchPattern);
		Matcher matcher = pattern.matcher(grid);
		if (matcher.find())
			return matcher.group(2);
		else
			return "ERROR: Cell not found";
	}
}