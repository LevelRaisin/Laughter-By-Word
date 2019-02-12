package characters;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * <b>Sprite</b> is a class that allows an Object to be represented by a
 * picture.
 * <p>
 * Changes: The class no longer supports animation of the <b>Sprite</b>.
 * <p>
 * Total Time Spent: 1 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class Sprite {
	/**
	 * An {@link Image images} that represents the <b>Sprite</b>.
	 */
	private Image character;

	/**
	 * Constructs an {@link Image images} from a directory.
	 * 
	 * @param name
	 *            The location of the {@link Image} required for the
	 *            <b>Sprite</b>.
	 */
	public Sprite(String name) {
		try {
			for (int i = 0; i < 4; i++) {
				character = ImageIO.read(new File("resources/graphics/characters/" + name + ".png"));
			}
		} catch (IOException ioe) {
			System.out.println(name);
		}
	}

	/**
	 * @return The {@link Image} representing the <b>Sprite</b>.
	 */
	public Image drawSprite() {
		return character;
	}
}
