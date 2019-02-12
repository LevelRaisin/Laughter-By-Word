package items;

/**
 * <b>ItemSet</b> is a combination of a weapon, a shield, and boots as defined
 * in {@link Item}. An <b>ItemSet</b> is provided to the
 * {@link characters.MainCharacter MainCharacter} because it removes the
 * necessity for direct interaction with the {@link Item items}.
 * <p>
 * Changes: The class now supports all interactions between
 * {@link characters.MainCharacter MainCharacter} and {@link ItemInventory}.
 * <p>
 * Total Time Spent: 3 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class ItemSet {
	/**
	 * The two {@link Item Items}: a weapon and a shield.
	 */
	private Item[] items;

	/**
	 * All the characters provided by the combination of the {@link Item Items}.
	 * Availability of characters is defined in {@link Item#charsAvail}. An
	 * <b>ItemSet</b> always provides 2 of each character.
	 */
	private int[] charsAvail;

	/**
	 * Creates an empty set of {@link Item Items}, following the two character
	 * rule of an <b>ItemSet</b>.
	 */
	public ItemSet() {
		items = new Item[2];
		items[0] = null;
		items[1] = null;

		charsAvail = new int[26];
		for (int i = 0; i < 26; i++) {
			charsAvail[i] = 2;
		}
	}

	/**
	 * @return An {@link Item} of type weapon.
	 */
	public Item getWeapon() {
		return items[0];
	}

	/**
	 * Change the weapon in the <b>ItemSet</b>.
	 * 
	 * @param newValue
	 *            An {@link Item} of type weapon.
	 */
	public void setWeapon(Item newValue) {
		if (items[0] != null)
			for (int i = 0; i < 26; i++) {
				charsAvail[i] -= items[0].getCharAvail(i);
			}
		items[0] = newValue;
		if (items[0] != null)
			for (int i = 0; i < 26; i++) {
				charsAvail[i] += items[0].getCharAvail(i);
			}
	}

	/**
	 * Unequips the current weapon.
	 * 
	 * @return The previously equipped weapon.
	 */
	public Item removeWeapon() {
		Item out = getWeapon();
		setWeapon(null);
		return out;
	}

	/**
	 * @return An {@link Item} of type shield.
	 */
	public Item getShield() {
		return items[1];
	}

	/**
	 * Change the shield in the <b>ItemSet</b>.
	 *
	 * @param newValue
	 *            An {@link Item} of type shield.
	 */
	public void setShield(Item newValue) {
		if (items[1] != null)
			for (int i = 0; i < 26; i++) {
				charsAvail[i] -= items[1].getCharAvail(i);
			}
		items[1] = newValue;
		if (items[1] != null)
			for (int i = 0; i < 26; i++) {
				charsAvail[i] += items[1].getCharAvail(i);
			}
	}

	/**
	 * Unequips the shield.
	 * 
	 * @return The previously equipped shield.
	 */
	public Item removeShield() {
		Item out = getShield();
		setShield(null);
		return out;
	}

	/**
	 * @return Damage of the weapon.
	 */
	public int getDamage() {
		return (items[0] == null ? 0 : items[0].getStat());
	}

	/**
	 * @return Defense of the shield.
	 */
	public int getDefense() {
		return (items[1] == null ? 0 : items[1].getStat()) / 6 + 15;
	}

	/**
	 * @return Characters provided by both the weapon and the shield.
	 */
	public int[] getCharsAvail() {
		return charsAvail;
	}

	/**
	 * Gets the amount of the char available at pos.
	 * 
	 * @param pos
	 *            position of the char being searched for.
	 * @return The amount of the character linked with pos available.
	 */
	public int getCharAvail(int pos) {
		return charsAvail[pos];
	}

	/**
	 * Gets the amount of the char which available.
	 * 
	 * @param which
	 *            the char being searched for
	 * @return The amount of the which char.
	 */
	public int getCharAvail(char which) {
		return charsAvail[String.valueOf(which).toUpperCase().charAt(0) - 'A'];
	}
}
