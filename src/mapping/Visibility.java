package mapping;

/**
 * <b>Visibility</b> is used to store how much visibility is provided by the
 * given tile.
 * <p>
 * Possible values:
 * <p>
 * {@code Visibility.ALL_BUT_CURRENT} : When stepping on this Tile, the
 * {@link characters.MainCharacter MainCharacter} is replaced with a red-x to
 * mark his spot, rather than his {@link characters.Sprite Sprite}.
 * <p>
 * {@code Visibility.ALL} : When stepping on this Tile, all other tiles remain
 * visible.
 * <p>
 * Changes: The class no longer supports any vision types except {@code ALL} and
 * {@code ALL_BUT_CURRENT}.
 * <p>
 * Total Time Spent: 0.5 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public enum Visibility {
	ALL_BUT_CURRENT, ALL;
}
