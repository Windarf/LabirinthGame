package labyrinth.results;

import labyrinth.util.repository.FileSystemRepository;
import lombok.NonNull;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class GameResultRepository extends FileSystemRepository<GameResult> {

    public GameResultRepository() {
        super(GameResult.class);
    }

    @Override
    public Set<GameResult> addOne(
            @NonNull final GameResult element) {

        if (element.getId() == null) {
            element.setId(getAll().stream()
                    .mapToLong(GameResult::getId)
                    .max()
                    .orElse(0L) + 1L);
        }
        element.setCreated(ZonedDateTime.now());
        return super.addOne(element);
    }

    /**
     * Returns the list of {@code n} best results with respect to the time
     * spent for solving the puzzle.
     *
     * @param n the maximum number of results to be returned
     * @return the list of {@code n} best results with respect to the time
     * spent for solving the puzzle
     */
    public List<GameResult> findBest(
            final int n) {

        return getAll().stream()
                .sorted(Comparator.comparing(GameResult::isSolved, Comparator.reverseOrder())
                        .thenComparing(GameResult::getSteps)
                        .thenComparing(GameResult::getDuration))
                .limit(n)
                .toList();
    }
}
