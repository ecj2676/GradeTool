package unitTests;

import java.security.InvalidParameterException;

import org.junit.Test;
import testHelp.*;

public class ExpressionSimplifierTests
{
	@Test
	public void SingleTokenIsUnchanged()
	{
		verify.that(ExpressionSimplifier.simplify("3")).isEqualTo(3.0);
	}

	@Test
	public void NegativeTokenIsUnchanged()
	{
		verify.that(ExpressionSimplifier.simplify("-2")).isEqualTo(-2.0);
	}

	@Test
	public void MultiDigitTokenIsUnchanged()
	{
		verify.that(ExpressionSimplifier.simplify("1024")).isEqualTo(1024.0);
	}

	@Test
	public void SimpleAdditionWorks()
	{
		verify.that(ExpressionSimplifier.simplify("3 + 5")).isEqualTo(8.0);
	}

	@Test
	public void SimpleSubtractionWorks()
	{
		verify.that(ExpressionSimplifier.simplify("4 - 7")).isEqualTo(-3.0);
	}

	@Test
	public void SimpleMultiplicationWorks()
	{
		verify.that(ExpressionSimplifier.simplify("19 * 3")).isEqualTo(57.0);
	}

	@Test
	public void SimpleDivisionWorks()
	{
		verify.that(ExpressionSimplifier.simplify("26 / 2")).isEqualTo(13.0);
	}

	@Test
	public void NegativeOperandsWork()
	{
		verify.that(ExpressionSimplifier.simplify("-4 - -2")).isEqualTo(-2.0);
	}

	@Test
	public void OrderOfOperationsIsCorrectForAddAndMultiply()
	{
		verify.that(ExpressionSimplifier.simplify("5 * 4 + 3 * 2")).isEqualTo(26.0);
	}

	@Test
	public void OrderOfOperationsIsCorrectForDivideAndSubtract()
	{
		verify.that(ExpressionSimplifier.simplify("10 - 9 / 3")).isEqualTo(7.0);
	}

	@Test
	public void DivisionIsLeftToRight()
	{
		verify.that(ExpressionSimplifier.simplify("27 / 3 / 3")).isEqualTo(3.0);
	}

	@Test
	public void SubtractionIsLeftToRight()
	{
		verify.that(ExpressionSimplifier.simplify("10 - 5 - 2")).isEqualTo(3.0);
	}

	@Test
	public void NonNumbersThrowException()
	{
		verify.that(() -> { ExpressionSimplifier.simplify("a + b"); }).throwsException().ofType(NumberFormatException.class);
	}

	@Test
	public void NonOperatorThrowsException()
	{
		verify.that(() -> { ExpressionSimplifier.simplify("3 x 7"); }).throwsException().ofType(InvalidParameterException.class);
	}
}
