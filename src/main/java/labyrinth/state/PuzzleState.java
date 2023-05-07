package labyrinth.state;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.StringJoiner;

/**
 * Represents the state of the puzzle.
 */
public class PuzzleState implements Cloneable {

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 14;

    /**
     * The index of the block.
     */
    public static final int BLOCK = 0;

    /**
     * The index of 2nd latest position of the BLOCK.
     */
    public static Position olderPosition = new Position(3,-2);
    /**
     * The index of the latest position of the BLOCK
     */
    public static Position oldPosition = new Position(3,-1);
    /**
     * The index of the current position of the BLOCK. It used for the rules.
     */
    public static Position currentPosition = new Position(3,0);
    /**
     * The index of the GOAL.
     */
    public static final Position GOAL = new Position(10,13);
    /**
     * An array to store the positions.
     */
    private Position[] positions;

    /**
     * Creates a {@code PuzzleState} object that corresponds to the original
     * initial state of the puzzle.
     */
    public PuzzleState() {
        this(new Position(3, 0),
                new Position(3, 0),
                new Position(1, 1),
                new Position(1, 7),
                new Position(1, 12),
                new Position(2, 5),
                new Position(2, 7),
                new Position(2, 9),
                new Position(3, 3),
                new Position(3, 9),
                new Position(4, 4),
                new Position(4, 10),
                new Position(5, 1),
                new Position(5, 6),
                new Position(5, 7),
                new Position(6, 6),
                new Position(6, 9),
                new Position(6, 11),
                new Position(7, 3),
                new Position(8, 2),
                new Position(8, 5),
                new Position(8, 8),
                new Position(8, 12),
                new Position(9, 7),
                new Position(10, 2),
                new Position(10, 3),
                new Position(10, 10),
                new Position(11, 4),
                new Position(11, 6),
                new Position(11, 8),
                new Position(11, 9),
                new Position(12, 1),
                new Position(12, 12));
        resetOldPos();
    }

    /**
     * Creates a {@code PuzzleState} object initializing the positions of the
     * pieces with the positions specified. The constructor expects an array of
     * four {@code Position} objects or four {@code Position} objects.
     *
     * @param positions the initial positions of the pieces
     */
    public PuzzleState(Position... positions) {
        checkPositions(positions);
        this.positions = deepClone(positions);
    }

    /**
     * Check whether the initial positions are in the board.
     * @param positions an array of positions to check.
     * @throws IllegalArgumentException if any of the postions are not on the board.
     */
    private void checkPositions(Position[] positions) {
        for (var position : positions) {
            if (!isOnBoard(position)) {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * {@return a copy of the position of the piece specified}
     *
     * @param n the number of a piece
     */
    public Position getPosition(int n) {
        return positions[n].clone();
    }

    /**
     * {@return whether the puzzle is solved}
     */
    public boolean isGoal() {
        return positions[BLOCK].equals(GOAL);
    }

    /**
     * Resets the positions of the step tracking variables.
     */
    public void resetOldPos(){
        olderPosition = new Position(3,-2);
        oldPosition = new Position(3,-1);
        currentPosition = new Position(3,0);
    }

    /**
     * {@return whether the block can be moved to the direction specified}
     *
     * @param direction a direction to which the block is intended to be moved
     */
    public boolean canMove(Direction direction) {
        return switch (direction) {
            case UP -> canMoveUp();
            case RIGHT -> canMoveRight();
            case DOWN -> canMoveDown();
            case LEFT -> canMoveLeft();
        };
    }

    private boolean canMoveUp() {

        if (positions[BLOCK].row() > 1 && isEmpty(positions[BLOCK].getUp())) {
            var tmp = oldPosition.clone();
            var tmp2 = olderPosition.clone();
            olderPosition = oldPosition.clone();
            oldPosition = currentPosition.clone();

            if (olderPosition.getUp().equals(oldPosition) || olderPosition.getLeft().equals(oldPosition)) {
                currentPosition = positions[BLOCK].getUp();
                return isEmpty(positions[BLOCK].getUp());
            }
            olderPosition = tmp2.clone();
            oldPosition = tmp.clone();
        }
        return false;
    }

    private boolean canMoveRight() {

        if (!positions[BLOCK].getRight().equals(GOAL)){
            if (positions[BLOCK].col() == BOARD_SIZE - 2) {
                return false;
            }
        }

        if (isEmpty(positions[BLOCK].getRight())) {
            var tmp = oldPosition.clone();
            var tmp2 = olderPosition.clone();
            olderPosition = oldPosition.clone();
            oldPosition = currentPosition.clone();

            if (olderPosition.getRight().equals(oldPosition) || olderPosition.getUp().equals(oldPosition)) {
                currentPosition = positions[BLOCK].getRight();
                return isEmpty(positions[BLOCK].getRight());
            }
            olderPosition = tmp2.clone();
            oldPosition = tmp.clone();
        }
        return false;
    }

    private boolean canMoveDown() {
        if (positions[BLOCK].row() == BOARD_SIZE - 2) {
            return false;
        }
        if (isEmpty(positions[BLOCK].getDown())) {
            var tmp = oldPosition.clone();
            var tmp2 = olderPosition.clone();
            olderPosition = oldPosition.clone();
            oldPosition = currentPosition.clone();

            if (olderPosition.getDown().equals(oldPosition) || olderPosition.getRight().equals(oldPosition)) {
                currentPosition = positions[BLOCK].getDown();
                return isEmpty(positions[BLOCK].getDown());
            }
            olderPosition = tmp2.clone();
            oldPosition = tmp.clone();
        }
        return false;
    }

    private boolean canMoveLeft() {

        if (positions[BLOCK].col() > 1 && isEmpty(positions[BLOCK].getLeft())) {
            var tmp = oldPosition.clone();
            var tmp2 = olderPosition.clone();
            olderPosition = oldPosition.clone();
            oldPosition = currentPosition.clone();

            if (olderPosition.getLeft().equals(oldPosition) || olderPosition.getDown().equals(oldPosition)) {
                currentPosition = positions[BLOCK].getLeft();
                return isEmpty(positions[BLOCK].getLeft());
            }
            olderPosition = tmp2.clone();
            oldPosition = tmp.clone();
        }
        return false;
    }

    /**
     * Moves the block to the direction specified.
     *
     * @param direction the direction to which the block is moved
     */
    public void move(Direction direction) {
        switch (direction) {
            case UP -> moveUp();
            case RIGHT -> moveRight();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
        }
    }

    private void moveUp() {
        positions[BLOCK].setUp();
    }

    private void moveRight() {
        positions[BLOCK].setRight();
    }

    private void moveDown() {
        positions[BLOCK].setDown();
    }

    private void moveLeft() {
        positions[BLOCK].setLeft();
    }

    /**
     * {@return the set of directions to which the block can be moved}
     */
    public EnumSet<Direction> getLegalMoves() {
        var legalMoves = EnumSet.noneOf(Direction.class);
        for (var direction : Direction.values()) {
            if (canMove(direction)) {
                legalMoves.add(direction);
            }
        }
        return legalMoves;
    }

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < BOARD_SIZE &&
                position.col() >= 0 && position.col() < BOARD_SIZE;
    }

    private boolean isEmpty(Position position) {
        for (var p : positions) {
            if (p.equals(position)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof PuzzleState ps) && Arrays.equals(positions, ps.positions);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(positions);
    }

    @Override
    public PuzzleState clone() {
        PuzzleState copy;
        try {
            copy = (PuzzleState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        copy.positions = deepClone(positions);
        return copy;
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(",", "[", "]");
        for (var position : positions) {
           sj.add(position.toString());
        }
        return sj.toString();
    }

    private static Position[] deepClone(Position[] a) {
        Position[] copy = a.clone();
        for (var i = 0; i < a.length; i++) {
            copy[i] = a[i].clone();
        }
        return copy;
    }

}
