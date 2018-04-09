package unitTests;

import java.util.ArrayList;
import org.junit.Test;
import testHelp.verify;

// focus on testing game state
public class Twos1Tests
{
	@Test
	public void gameStateCanBeConstructed()
	{
		GameState gs = new GameState();
		verify.that(gs).isOfType(GameState.class);
	}
	
	@Test
	public void newGamePopulatesValuesWithRandomNumbers()
	{
		GameState gs = new GameState();
		gs.newGame();
		
		for (int i = 0; i < 16; i++)
		{
			verify.that(gs.getValue(i / 4, i % 4) < 16).isTrue(String.format("random value at r=%d, c=%d should be less than 16", i / 4, i % 4));
			verify.that(gs.getValue(i / 4, i % 4) >= 0).isTrue(String.format("random value at r=%d, c=%d should be greater than or equal to zero", i / 4, i % 4));
		}
	}
	
	@Test
	public void newGameGeneratesDifferentNumbers()
	{
		GameState gs = new GameState();
		gs.newGame();
		
		int[] firstGameNumbers = getValues(gs);
		gs.newGame();
		int[] secondGameNumbers = getValues(gs);
		
		int same = 0;
		for (int i = 0; i < 16; i++)
		{
			if (firstGameNumbers[i] == secondGameNumbers[i])
				same++;
		}
		
		verify.that(same < 16).isTrue("all 16 values should not be the same after newGame() is called");
		
		
		
	}
	
	@Test
	public void newGameSetsStatusCorrectly()
	{
		GameState gs = new GameState();
		gs.newGame();
		
		verify.that(gs.getStatus()).isEqualTo("New game started");
	}
	
	@Test
	public void newGameSetsScoreCorrectly()
	{
		GameState gs = new GameState();
		gs.newGame();
		
		verify.that(gs.getScore()).isEqualTo(0);
	}
	
	@Test
	public void shiftLeftUpdatesStatusCorrectly()
	{
		GameState gs = new GameState();
		gs.shift(GameState.LEFT);
		verify.that(gs.getStatus()).isEqualTo("Shifted tiles left");
	}

	@Test
	public void shiftRightUpdatesStatusCorrectly()
	{
		GameState gs = new GameState();
		gs.shift(GameState.RIGHT);
		verify.that(gs.getStatus()).isEqualTo("Shifted tiles right");
	}

	@Test
	public void shiftUpUpdatesStatusCorrectly()
	{
		GameState gs = new GameState();
		gs.shift(GameState.UP);
		verify.that(gs.getStatus()).isEqualTo("Shifted tiles up");
	}

	@Test
	public void shiftDownUpdatesStatusCorrectly()
	{
		GameState gs = new GameState();
		gs.shift(GameState.DOWN);
		verify.that(gs.getStatus()).isEqualTo("Shifted tiles down");
	}
	
	class MockListener implements IChangeListener
	{
		public int redrawCallCount;
		public void redraw() { redrawCallCount++; }
	}
	
	@Test
	public void shiftUpdatesOneListener()
	{
		GameState gs = new GameState();
		MockListener listener = new MockListener();
		gs.addListener(listener);
		gs.shift(GameState.LEFT);
		
		verify.that(listener.redrawCallCount == 1).isTrue("redraw() should have been called exactly once during shift()");
	}

	@Test
	public void shiftUpdatesMultipleListeners()
	{
		GameState gs = new GameState();
		MockListener[] ears = { new MockListener(), new MockListener(), new MockListener() };
		for (IChangeListener icl : ears)
		{
			gs.addListener(icl);
		}
		
		gs.shift(GameState.LEFT);
		
		for (MockListener ml : ears)
		{
			verify.that(ml.redrawCallCount == 1).isTrue("redraw() should have been called exactly once during shift() for each IChangeListener");		
		}
	}

	@Test
	public void newGameUpdatesListener()
	{
		GameState gs = new GameState();
		MockListener listener = new MockListener();
		gs.addListener(listener);
		gs.newGame();
		
		verify.that(listener.redrawCallCount == 1).isTrue("redraw() should have been called exactly once during newGame()");
	}
	
	private int[] getValues(GameState gs)
	{
		int[] values = new int[16];
		for (int i = 0; i < 16; i++)
			values[i] = gs.getValue(i / 4, i % 4);
		return values;
	}
}
