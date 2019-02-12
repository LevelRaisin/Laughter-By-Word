package items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * <b>GemInventory</b> is a collection of {@link items.GemInventory.Gem gems}
 * available to the player.
 * 
 * It stores it in an {@link ArrayList} of {@link GemInventory.Gem}. It also
 * ensures that there are at most 9 Gems.
 * <p>
 * Changes: The class now properly renders, and provides better interaction
 * between {@link items.GemInventory.Gem Gem} and other classes.
 * <p>
 * Total Time Spent: 1 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class GemInventory {
	/**
	 * <b>Gem</b> is the class used to represent a singular <b>Gem</b>.
	 * 
	 * A <b>Gem</b> is a combination of a value of the <b>Gem</b>, which could
	 * be positive or negative, and a the type of weapon it can enhance.
	 * 
	 * @author Lev Raizman
	 * @version 2.0.0.0
	 *
	 */
	public class Gem {
		/**
		 * The value provided by the <b>Gem</b>.
		 */
		private int value;

		/**
		 * The type of weapon the <b>Gem</b> supports.
		 */
		private boolean weapon;

		/**
		 * Constructs the <b>Gem</b> using all needed values.
		 * 
		 * @param value
		 *            the boost granted by the <b>Gem</b>
		 * @param type
		 *            true if for weapon, false if for shield
		 */
		public Gem(int value, boolean type) {
			this.value = value;
			weapon = type;
		}

		/**
		 * @return The boost of the <b>Gem</b>.
		 */
		public int getValue() {
			return value;
		}

		/**
		 * @return True if this <b>Gem</b> enhances a weapon, false if this
		 *         <b>Gem</b> enhances a shield.
		 */
		public boolean getType() {
			return weapon;
		}

		/**
		 * Creates an {@link java.awt.Image image} of the <b>Gem</b> and returns
		 * it.
		 * <p>
		 * The <b>Gem</b> is green if it has a positive value, red if negative.
		 * The <b>Gem</b> is a square if it supports a weapon, a circle if it
		 * supports a shield.
		 * 
		 * @return An {@link java.awt.Image image} of the current <b>Gem</b>.
		 */
		public Image drawGem() {
			Image out = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < out.getWidth(null); i++)
				for (int j = 0; j < out.getHeight(null); j++)
					((BufferedImage) out).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());

			Graphics g = out.getGraphics();
			if (value < 0)
				g.setColor(new Color(255, 0, 0));
			else
				g.setColor(new Color(0, 255, 0));

			if (weapon)
				g.fillRect(10, 10, 20, 20);
			else
				g.fillOval(10, 10, 20, 20);

			return out;
		}
	}

	/**
	 * The <b>GemInventory</b> of the {@link items.GemInventory.Gem gems}.
	 */
	ArrayList<Gem> gems;

	/**
	 * Creates an empty <b>GemInventory</b>
	 */
	public GemInventory() {
		gems = new ArrayList<Gem>();
	}

	/**
	 * Adds the {@link items.GemInventory.Gem gem} m to the <b>GemInventory</b>.
	 * If the <b>GemInventory</b> is full, it replaces the last
	 * {@link items.GemInventory.Gem gem}.
	 * 
	 * @param m
	 *            the {@link items.GemInventory.Gem gem} to be added.
	 */
	public void addGem(Gem m) {
		if (gems.size() < 9) {
			gems.add(m);
		} else {
			gems.set(9, m);
		}
	}

	/**
	 * Removes the {@link items.GemInventory.Gem gem} at pos, and returns it.
	 * 
	 * @param pos
	 *            the position of the {@link items.GemInventory.Gem gem} being
	 *            used
	 * @return The used {@link items.GemInventory.Gem gem}.
	 */
	public Gem useGem(int pos) {
		return gems.remove(pos);
	}

	/**
	 * @return All the {@link items.GemInventory.Gem gems} in the
	 *         <b>GemInventory</b>.
	 */
	public ArrayList<Gem> getGems() {
		return gems;
	}
}
