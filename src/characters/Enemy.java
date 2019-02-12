package characters;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * <b>Enemy</b> provides use of the unnplayable characters that the
 * {@link MainCharacter} has to defeat.
 * 
 * It provides an array of all the names of the different enemies, as well as
 * certain parameters required to battle them.
 * <p>
 * Changes: The class now supports all aspects of combat, and no longer crashes
 * on "Sneaky Assassin".
 * <p>
 * Total Time Spent: 1.5 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class Enemy {
	/**
	 * All the possible names of the <b>enemies</b>. The first layer represents
	 * the level they can be encountered in.
	 */
	public final static String[][] NAMES = {
			{ "Radiated Mouse", "Land Based Starfish", "Animal Seaweed", "Demonic Bat" },
			{ "Spirit of a Haunted Tree", "Five Fears of Death", "Sneaky Assassin", "Foul Howling Owl" },
			{ "Magically Modified Mouse", "Starfish of the Rocks", "Spirit of Cave Worshiper", "Zero Gravity Bat" },
			{ "Blague Rival of Shutka", "Broma Friend of Shutka", "Nenavist' the God of Hate" } };

	/**
	 * The color of the <b>Enemy</b>, required for battling in level 3.
	 */
	private Color col;

	/**
	 * The damage that needs to be dealt to defeat the <b>Enemy</b>.
	 */
	private int healthPoints;

	/**
	 * The visual representation of the <b>Enemy</b>.
	 */
	private Sprite image;

	/**
	 * The {@link java.awt.Image image} after a single call of the draw
	 * function, with {@link Enemy#col color} added.
	 */
	private Image newImage;

	/**
	 * The name of the <b>Enemy</b>.
	 */
	private String name;

	/**
	 * Defines all the parameters of the <b>Enemy</b>.
	 * 
	 * @param col
	 *            {@link java.awt.Color} of the <b>Enemy</b>.
	 * @param healthPoints
	 *            health of the <b>Enemy</b>.
	 * @param name
	 *            name of the enemy. Only use this in conjunction with
	 *            {@link Enemy#NAMES}.
	 */
	public Enemy(Color col, int healthPoints, String name) {
		this.col = col;
		this.healthPoints = healthPoints;
		this.name = name;
		image = new Sprite(name);
		newImage = null;
	}

	/**
	 * @return The name of the <b>Enemy</b>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The {@link java.awt.Image image} of the <b>Enemy</b>.
	 */
	public Image drawEnemy() {
		if (newImage == null) {
			Image temp = image.drawSprite();
			newImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_ARGB);

			for (int i = 0; i < newImage.getWidth(null); i++) {
				for (int j = 0; j < newImage.getHeight(null); j++) {
					int pixel = ((BufferedImage) temp).getRGB(i, j);
					if ((pixel >> 24) != 0x00)
						((BufferedImage) newImage).setRGB(i, j, (Math.random() < 0.25 ? col.getRGB() : pixel));
					else
						((BufferedImage) newImage).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
				}
			}
		}

		return newImage;
	}

	/**
	 * @return The health of the <b>Enemy</b>.
	 */
	public int getHP() {
		return healthPoints;
	}

	/**
	 * Deals damage to the <b>Enemy</b>. Used after a spell cast.
	 * 
	 * @param damage
	 *            the damage caused by the spell
	 * 
	 */
	public void takeDamage(int damage) {
		healthPoints -= damage;
	}

	/**
	 * This calculates damage dealt by a spell, by taking differences in hue.
	 * 
	 * @param newCol
	 *            the {@link java.awt.Color color} of the spell cast.
	 * @return The total damage dealt by the spell.
	 */
	public int compareColor(Color newCol) {
		if (newCol == null)
			return 0;
		float hThis = Color.RGBtoHSB(col.getRed(), col.getBlue(), col.getGreen(), null)[0];
		float hSpell = Color.RGBtoHSB(newCol.getRed(), newCol.getBlue(), newCol.getGreen(), null)[0];
		return healthPoints / 2 - (int) (((hThis * hSpell) % 1) * ((Math.log10(healthPoints) + 1) * 10));
	}
}
