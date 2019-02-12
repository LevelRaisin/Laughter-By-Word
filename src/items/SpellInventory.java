package items;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * <b>SpellInventory</b> is a collection of Spells available to the player.
 * 
 * It stores it in an {@link ArrayList} of {@link java.awt.Color colors}. It
 * also ensures that there are at most 9 Spells.
 * <p>
 * Changes: The class now supports proper rendering of spells.
 * <p>
 * Total Time Spent: 1 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class SpellInventory {
	/**
	 * The base {@link java.awt.Image image} of a Spell.
	 */
	private static Image spellImage = initImage();

	/**
	 * The {@link java.awt.Color colors} for the spells.
	 */
	ArrayList<Color> spells;

	/**
	 * Initializes an empty <b>SpellInventory</b>.
	 */
	public SpellInventory() {
		spells = new ArrayList<Color>();
	}

	/**
	 * Adds a spell to the <b>SpellInventory</b>.
	 * 
	 * @param m
	 *            the spell to be added.
	 */
	public void addSpell(Color m) {
		if (spells.size() < 9) {
			spells.add(m);
		} else {
			spells.set(9, m);
		}
	}

	/**
	 * Removes the spell at pos from the <b>SpellInventory</b>, and returns it.
	 * 
	 * @param pos
	 *            the position of the spell used.
	 * @return The spell at pos.
	 */
	public Color useSpell(int pos) {
		return spells.remove(pos);
	}

	/**
	 * @return All the spells in the <b>SpellInventory</b>.
	 */
	public ArrayList<Color> getSpells() {
		return spells;
	}

	/**
	 * Switches the spell at pos with the last spell, if the inventory is full.
	 * 
	 * @param pos
	 *            the pos of the spell to be swapped.
	 */
	public void swapSpells(int pos) {
		if (spells.size() < 9)
			return;
		Color temp = spells.get(pos);
		spells.set(pos, spells.get(8));
		spells.set(8, temp);
	}

	/**
	 * Returns an {@link java.awt.Image image} of the spell, an alteration of
	 * {@link SpellInventory#spellImage} to the {@link java.awt.Color color} of
	 * the spell.
	 * 
	 * @param spellCol
	 *            the {@link java.awt.Color color} of the spell being used.
	 * @return An altered version of {@link SpellInventory#spellImage}.
	 */
	public static Image drawSpell(Color spellCol) {
		Image out = spellImage;

		for (int i = 0; i < out.getWidth(null); i++) {
			for (int j = 0; j < out.getHeight(null); j++) {
				int pixel = ((BufferedImage) out).getRGB(i, j);
				if ((pixel >> 24) != 0x00)
					((BufferedImage) out).setRGB(i, j, spellCol.getRGB());
			}
		}

		return out;
	}

	/**
	 * Initializes the regular spell image.
	 * 
	 * @return The base {@link java.awt.Image image} associated with a spell.
	 */
	private static Image initImage() {
		try {
			return ImageIO.read(new File("resources/graphics/misc/spell.png"));
		} catch (IOException e) {
			return null;
		}
	}
}
