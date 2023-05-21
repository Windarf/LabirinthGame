package labyrinth.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PuzzleStateTest {

    PuzzleState originalState = new PuzzleState();

    PuzzleState goalState = new PuzzleState(new Position(10, 13));

    PuzzleState nonGoalState = new PuzzleState(new Position(5, 5));

    PuzzleState deadEndState = new PuzzleState(
            new Position(5,5),
            new Position(4,5),
            new Position(6,5),
            new Position(5,4),
            new Position(5,6));


    @Test
    void isGoal() {
        assertFalse(originalState.isGoal());
        assertTrue(goalState.isGoal());
        assertFalse(nonGoalState.isGoal());
        assertFalse(deadEndState.isGoal());
    }
}