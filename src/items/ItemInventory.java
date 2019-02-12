package items;

import java.util.ArrayList;

/**
 * <b>ItemInventory</b> is used for storage of all {@link Item items} available
 * to the {@link characters.MainCharacter player}.
 * <p>
 * This includes weapons and shields, as well as the letters, keys, and the
 * torch available to the {@link characters.MainCharacter player}.
 * <p>
 * Changes: The class now supports proper interchange between {@link Item items}
 * given, and existing {@link Item items}.
 * <p>
 * Total Time Spent: 2 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class ItemInventory {
	/**
	 * All the {@link Item items} available to the
	 * {@link characters.MainCharacter player}.
	 */
	private ArrayList<Item> items;

	/**
	 * The amount of keys the player has.
	 */
	private int keys;

	/**
	 * Whether or not the {@link characters.MainCharacter player} has a torch.
	 */
	private boolean torch;

	/**
	 * The letters the {@link characters.MainCharacter player} has already
	 * found.
	 */
	private int[] letters;

	/**
	 * Creates an empty <b>ItemInventory</b>.
	 */
	public ItemInventory() {
		items = new ArrayList<Item>();
		keys = 0;
		torch = false;
		letters = new int[14];
		letters[13] = 0;
	}

	/**
	 * Adds an {@link Item} to the <b>ItemInventory</b> if it is not full, or
	 * replaces the last {@link Item} if full.
	 * 
	 * @param m
	 *            the {@link Item} being added.
	 */
	public void addItem(Item m) {
		if (m.getName().equals("Key")) {
			keys++;
			return;
		} else if (m.getName().equals("Torch")) {
			torch = true;
			return;
		}

		if (items.size() < 23)
			items.add(m);
		else
			items.set(22, m);
	}

	/**
	 * Adds a letter to the letter array, and increments the amount of letters.
	 * 
	 * @param letter
	 *            the letter to be added.
	 */
	public void addLetter(char letter) {
		letters[letters[13]] = letter;
		letters[13]++;
	}

	/**
	 * @return The letters the user has found, in the order they were found.
	 */
	public String getLetters() {
		String out = "";
		for (int i = 0; i < letters[13]; i++) {
			out += (char) letters[i];
		}
		return out;
	}

	/**
	 * Sets the {@link Item} at pos to newItem and returns the previously stored
	 * value.
	 * 
	 * @param newItem
	 *            {@link Item} being added.
	 * @param pos
	 *            position for {@link Item} to be added to.
	 * @return The {@link Item} previously at pos.
	 */
	public Item switchWith(Item newItem, int pos) {
		Item out = items.remove(pos);
		if (newItem != null)
			items.add(newItem);
		return out;
	}

	/**
	 * The sum of vision of the player. The torch gives one vision, and the
	 * Fulvus Lance gives another.
	 * 
	 * @return The radius of player vision.
	 */
	public int increaseInVision() {
		int out = (torch ? 1 : 0);

		for (Item m : items) {
			if (m.getName().contains("Fulvus Lance")) {
				out += 1;
				break;
			}
		}

		return out;
	}

	/**
	 * If a key exists, decrements amount of keys and returns true.
	 * 
	 * @return Whether there previously was a key.
	 */
	public boolean getKey() {
		return (keys > 0 ? --keys >= 0 : false);
	}

	/**
	 * @return The amount of keys the player has.
	 */
	public int getKeys() {
		return keys;
	}

	/**
	 * @return Whether the user has a torch or not.
	 */
	public boolean getTorch() {
		return torch;
	}

	/**
	 * @return The Items in inventory.
	 */
	public ArrayList<Item> getItems() {
		return items;
	}
}
