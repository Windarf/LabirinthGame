package labyrinth.state;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PuzzleStateTest {

    PuzzleState originalState = new PuzzleState();
    PuzzleState goalState = new PuzzleState(new Position(10, 13));
    PuzzleState nonGoalState = new PuzzleState(
            new Position(5, 5),
            new Position(1,1),
            new Position(10,10));
    PuzzleState deadEndState = new PuzzleState(
            new Position(3,4),
            new Position(3,3),
            new Position(4,4));

    @Test
    void constructor() {
        var positions = new Position[] {
                new Position(0, 0),
                new Position(2, 0),
                new Position(1, 1),
                new Position(0, 2),
                new Position(2,2)
        };
        PuzzleState state = new PuzzleState(positions);
        for (var i = 0; i < 5; i++) {
            assertEquals(positions[i], state.getPosition(i));
            assertNotSame(positions[i], state.getPosition(i));
        }
    }
    @Test
    void constructor_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(-1, -1)));
        assertThrows(NullPointerException.class, () -> new PuzzleState(null,null));
    }
    @Test
    void isGoal() {
        assertFalse(originalState.isGoal());
        assertTrue(goalState.isGoal());
        assertFalse(nonGoalState.isGoal());
        assertFalse(deadEndState.isGoal());
    }

    @Test
    void canMove_originalState(){
        assertFalse(originalState.canMove(Direction.UP));
        assertTrue(originalState.canMove(Direction.RIGHT));
        assertFalse(originalState.canMove(Direction.DOWN));
        assertFalse(originalState.canMove(Direction.LEFT));
    }
    @Test
    void canMove_goalState(){
        assertFalse(goalState.canMove(Direction.UP));
        assertFalse(goalState.canMove(Direction.RIGHT));
        assertFalse(goalState.canMove(Direction.DOWN));
        assertFalse(goalState.canMove(Direction.RIGHT));
    }
    @Test
    void canMove_nonGoalState(){
        nonGoalState.setPreviousPosition(new Position(5,4));
        assertFalse(nonGoalState.canMove(Direction.UP));
        assertTrue(nonGoalState.canMove(Direction.RIGHT));
        assertTrue(nonGoalState.canMove(Direction.DOWN));
        assertFalse(nonGoalState.canMove(Direction.LEFT));
    }
    @Test
    void canMove_deadEndState(){
        deadEndState.setPreviousPosition(new Position(2,4));
        assertFalse(deadEndState.canMove(Direction.UP));
        assertFalse(deadEndState.canMove(Direction.RIGHT));
        assertFalse(deadEndState.canMove(Direction.DOWN));
        assertFalse(deadEndState.canMove(Direction.LEFT));
    }
    @Test
    void move_right_originalState() {
        var copy = originalState.clone();
        originalState.move(Direction.RIGHT);
        assertEquals(copy.getPosition(0).getRight(), originalState.getPosition(0));
        assertEquals(copy.getPosition(1), originalState.getPosition(1));
        assertEquals(copy.getPosition(2), originalState.getPosition(2));
    }
    @Test
    void move_up_nonGoalState() {
        var copy = nonGoalState.clone();
        nonGoalState.setPreviousPosition(new Position(5,6));
        nonGoalState.move(Direction.UP);
        assertEquals(copy.getPosition(0).getUp(), nonGoalState.getPosition(0));
        assertEquals(copy.getPosition(1), nonGoalState.getPosition(1));
        assertEquals(copy.getPosition(2), nonGoalState.getPosition(2));
    }
    @Test
    void move_right_nonGoalState() {
        var copy = nonGoalState.clone();
        nonGoalState.setPreviousPosition(new Position(5,4));
        nonGoalState.move(Direction.RIGHT);
        assertEquals(copy.getPosition(0).getRight(), nonGoalState.getPosition(0));
        assertEquals(copy.getPosition(1), nonGoalState.getPosition(1));
        assertEquals(copy.getPosition(2), nonGoalState.getPosition(2));
    }
    @Test
    void move_down_nonGoalState() {
        var copy = nonGoalState.clone();
        nonGoalState.setPreviousPosition(new Position(5,4));
        nonGoalState.move(Direction.DOWN);
        assertEquals(copy.getPosition(0).getDown(), nonGoalState.getPosition(0));
        assertEquals(copy.getPosition(1), nonGoalState.getPosition(1));
        assertEquals(copy.getPosition(2), nonGoalState.getPosition(2));
    }
    @Test
    void move_left_nonGoalState() {
        var copy = nonGoalState.clone();
        nonGoalState.setPreviousPosition(new Position(5,6));
        nonGoalState.move(Direction.LEFT);
        assertEquals(copy.getPosition(0).getLeft(), nonGoalState.getPosition(0));
        assertEquals(copy.getPosition(1), nonGoalState.getPosition(1));
        assertEquals(copy.getPosition(2), nonGoalState.getPosition(2));
    }
    @Test
    void getLegalMoves() {
        nonGoalState.setPreviousPosition(new Position(5,4));
        deadEndState.setPreviousPosition(new Position(2,4));
        assertEquals(EnumSet.of(Direction.RIGHT), originalState.getLegalMoves());
        assertEquals(EnumSet.noneOf(Direction.class), goalState.getLegalMoves());
        assertEquals(EnumSet.of(Direction.RIGHT, Direction.DOWN), nonGoalState.getLegalMoves());
        assertEquals(EnumSet.noneOf(Direction.class), deadEndState.getLegalMoves());
    }
    @Test
    void isEmpty(){
        Position emptyPosition = new Position(3, 1);
        assertTrue(originalState.isEmpty(emptyPosition));

        Position nonEmptyPosition  = originalState.getPosition(0);
        assertFalse(originalState.isEmpty(nonEmptyPosition));

    }





    @Test
    void testEquals() {
        assertTrue(originalState.equals(originalState));

        var clone = originalState.clone();
        clone.move(Direction.RIGHT);
        assertFalse(clone.equals(originalState));

        assertFalse(originalState.equals(null));
        assertFalse(originalState.equals("Hello, World!"));
        assertFalse(originalState.equals(nonGoalState));
    }
    @Test
    void testHashCode() {
        assertTrue(originalState.hashCode() == originalState.hashCode());
        assertTrue(originalState.hashCode() == originalState.clone().hashCode());
    }

    @Test
    void testClone() {
        var clone = originalState.clone();
        assertTrue(clone.equals(originalState));
        assertNotSame(clone, originalState);
    }

    @Test
    void testToString() {
        assertEquals("(3,0)", originalState.toString());
        assertEquals("(10,13)", goalState.toString());
        assertEquals("(5,5)", nonGoalState.toString());
        assertEquals("(3,4)", deadEndState.toString());
    }
}