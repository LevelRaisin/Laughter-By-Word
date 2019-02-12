package characters;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import items.ItemInventory;
import items.GemInventory;
import items.Item;
import items.ItemSet;
import items.SpellInventory;

/**
 * This class represents the character that the player has access to.
 * <p>
 * This character is a playable character, as opposed to all the other
 * unplayable characters. The class combines a set of {@link Sprite Sprites}
 * with an {@link items.ItemSet ItemSet}, {@link items.ItemInventory
 * ItemInventory}, {@link items.GemInventory GemInventory}, and
 * {@link items.SpellInventory SpellInventory}.
 * <p>
 * Changes: The class now supports all interactions between the
 * <b>MainCharacter</b>, all three inventories, and {@link frame.GameGraphics
 * graphics}.
 * <p>
 * Total Time Spent: 2.5 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 */
public class MainCharacter {
	/**
	 * A group of {@link Sprite Sprites} that represents the
	 * <b>MainCharacter</b>, facing one of the orthogonal directions.
	 */
	private Sprite[] image;

	/**
	 * The {@link items.ItemSet ItemSet} available to the <b>MainCharacter</b>.
	 */
	private ItemSet items;

	/**
	 * The {@link items.ItemInventory ItemInventory} available to the
	 * <b>MainCharacter</b>.
	 */
	private ItemInventory itemInventory;

	/**
	 * The {@link items.GemInventory GemInventory} available to the
	 * <b>MainCharacter</b>.
	 */
	private GemInventory gemInventory;

	/**
	 * The {@link items.SpellInventory SpellInventory} available to the
	 * <b>MainCharacter</b>.
	 */
	private SpellInventory spellInventory;

	/**
	 * The {@link items.GemInventory.Gem Gems} equipped by the
	 * <b>MainCharacter</b>.
	 */
	private GemInventory.Gem[] gems;

	/**
	 * The direction the character is facing.
	 */
	private Point dir;

	/**
	 * Constructs a new <b>MainCharacter</b>.
	 */
	public MainCharacter() {
		image = new Sprite[4];
		image[0] = new Sprite("mainUp");
		image[1] = new Sprite("mainDown");
		image[2] = new Sprite("mainLeft");
		image[3] = new Sprite("mainRight");
		dir = new Point(0, 1);
		this.items = new ItemSet();
		this.itemInventory = new ItemInventory();
		this.gemInventory = new GemInventory();
		this.spellInventory = new SpellInventory();
		gems = new GemInventory.Gem[2];
	}

	/**
	 * @return The {@link Sprite sprite's} rendering of the
	 *         <b>MainCharacter</b>.
	 */
	public Image drawCharacter() {
		if (dir.y == -1)
			return image[0].drawSprite();
		else if (dir.y == 1)
			return image[1].drawSprite();
		else if (dir.x == -1)
			return image[2].drawSprite();
		else if (dir.x == 1)
			return image[3].drawSprite();
		return null;
	}

	/**
	 * @return Provides access to the <b>MainCharacter</b>'s
	 *         {@link items.ItemSet ItemSet}.
	 */
	public ItemSet getItemSet() {
		return items;
	}

	/**
	 * @return Provides access to the <b>MainCharacter</b>'s
	 *         {@link items.ItemInventory ItemInventory}.
	 */
	public ItemInventory getInventory() {
		return itemInventory;
	}

	/**
	 * @return Provides access to the <b>MainCharacter</b>'s
	 *         {@link items.GemInventory GemInventory}.
	 */
	public GemInventory getGems() {
		return gemInventory;
	}

	/**
	 * @return Provides access to the <b>MainCharacter</b>'s
	 *         {@link items.SpellInventory SpellInventory}.
	 */
	public SpellInventory getSpells() {
		return spellInventory;
	}

	/**
	 * @return Provides access to the <b>MainCharacter</b>'s
	 *         {@link items.GemInventory.Gem Gems} equipped.
	 */
	public GemInventory.Gem[] getEquipedGems() {
		return gems;
	}

	/**
	 * Changes the direction the player is facing.
	 * 
	 * @param dir
	 *            a direction where either the x or y is set to 1 or -1,
	 *            representing all directions.
	 */
	public void setDir(Point dir) {
		this.dir = new Point(dir);
	}

	/**
	 * Adds an {@link items.Item item} to the {@link items.ItemInventory
	 * inventory}.
	 * 
	 * @param m
	 *            the {@link items.Item item} to be added.
	 */
	public void addItem(Item m) {
		itemInventory.addItem(m);
	}

	/**
	 * Switches the {@link items.Item item} at pos with the equipped
	 * {@link items.Item item}.
	 * 
	 * @param pos
	 *            the {@link items.Item item} to be swapped with the equipped
	 *            {@link items.Item item}.
	 */
	public void switchItems(int pos) {
		if (itemInventory.getItems().size() <= pos)
			return;
		Item m = itemInventory.getItems().get(pos);
		Boolean weapon = (m == null ? null : m.getType());
		if (weapon == null)
			return;
		if (weapon) {
			items.setWeapon(itemInventory.switchWith(items.getWeapon(), pos));
		} else if (!weapon) {
			items.setShield(itemInventory.switchWith(items.getShield(), pos));
		}
	}

	/**
	 * Unequips an equipped {@link items.Item item} based on the value of
	 * weapon.
	 * 
	 * @param weapon
	 *            whether to unequip the weapon or the shield.
	 */
	public void unequip(boolean weapon) {
		if (weapon && items.getWeapon() != null) {
			itemInventory.addItem(items.removeWeapon());
		} else if (!weapon && items.getShield() != null) {
			itemInventory.addItem(items.removeShield());
		}
	}

	/**
	 * Adds a randomly generated {@link items.GemInventory.Gem gem} to the
	 * {@link items.GemInventory gem inventory}.
	 */
	public void addGem() {
		gemInventory.addGem(gemInventory.new Gem((int) (Math.random() * 14 + 1) * (Math.random() < 0.5 ? 1 : -1),
				Math.random() < 0.5));
	}

	/**
	 * Equips the {@link items.GemInventory.Gem gem} at pos.
	 * 
	 * @param pos
	 *            the position of the {@link items.GemInventory.Gem gem} being
	 *            equipped.
	 */
	public void setGem(int pos) {
		if (gemInventory.getGems().size() <= pos)
			return;
		GemInventory.Gem m = gemInventory.useGem(pos);
		boolean weapon = (m == null ? null : m.getType());
		if (weapon)
			gems[0] = m;
		else
			gems[1] = m;
	}

	/**
	 * Adds a randomly generated spell to the {@link items.SpellInventory spell
	 * inventory}.
	 */
	public void addSpell() {
		spellInventory.addSpell(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
	}

	/**
	 * Removes the spell at pos from the {@link items.SpellInventory spell
	 * inventory}.
	 * 
	 * @param pos
	 *            the position of the spell being used.
	 * @return The {@link java.awt.Color} of the used spell.
	 */
	public Color useSpell(int pos) {
		if (spellInventory.getSpells().size() <= pos)
			return null;
		return spellInventory.getSpells().remove(pos);
	}

	/**
	 * Switches the spell at pos with the last spell.
	 * 
	 * @param pos
	 *            the position of the spell being swapped.
	 */
	public void swapSpells(int pos) {
		spellInventory.swapSpells(pos);
	}
}
