package items;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

/**
 * <b>Item</b> is the representation of one <b>Item</b> used by the player.
 * <p>
 * An <b>Item</b> is represented by its name, its {@link Image}, the characters
 * it provides to the player, and its stat. An <b>Item</b> can be one of two
 * types: a weapon or a shield. The stat can be one of: damage or defence. Each
 * type represents a stat respectively.
 * <p>
 * <b>Item</b> also contains everything required to provide the
 * {@link characters.MainCharacter player} with <b>Item</b>s, and make sure that
 * the {@link characters.MainCharacter player} doesn't get duplicates.
 * <p>
 * Changes: The class now supports updated specifications for type and proper
 * rendering.
 * <p>
 * Total Time Spent: 2 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class Item {
	/**
	 * All <b>Item</b>s that exist in the game.
	 */
	private static ArrayList<Item>[] allItems = initAllItems();

	/**
	 * All the letters needed to complete the game.
	 */
	private static char[] letters = initLetters();

	/**
	 * The name of the <b>Item</b>.
	 */
	protected String name;

	/**
	 * The picture representation of the <b>Item</b>.
	 */
	private Image drawing;

	/**
	 * The characters this <b>Item</b> provides to the player in combat. Each
	 * position in the array corresponds to a respective character, starting
	 * from {@code 'A'}.
	 */
	private int[] charsAvail;

	/**
	 * The value of the stat provided by this <b>Item</b> which corresponds to
	 * the <b>Item</b>s type.
	 */
	private int stat;

	/**
	 * True if this is a weapon, false if it is a shield, null if it is neither.
	 */
	private Boolean weapon;

	/**
	 * The description of the <b>Item</b>.
	 */
	private String description;

	/**
	 * -1 if the <b>Item</b> subtracts the value of the Gem, +1 if it adds it.
	 */
	private int gemSlotType;

	/**
	 * Creates an <b>Item</b> by taking everything that the <b>Item</b> needs.
	 * 
	 * @param name
	 *            name of the <b>Item</b>.
	 * @param type
	 *            type of the <b>Item</b> as specified by weapon.
	 * @param statVal
	 *            the strength of the <b>Item</b>.
	 * @param description
	 *            the description of the <b>Item</b>.
	 * @param charsAvail
	 *            the characters provided by this <b>Item</b>.
	 * @param gemType
	 *            the type of slot the weapon has, as specified by
	 *            {@link Item#gemSlotType}.
	 */
	public Item(String name, Boolean type, int statVal, String description, int[] charsAvail, int gemType) {
		try {
			this.drawing = ImageIO.read(new File("resources/graphics/items/" + name + ".png"));
		} catch (IOException e) {
		}
		this.name = name;
		stat = statVal;
		weapon = (type == null ? null : type.booleanValue());
		this.description = description;
		this.charsAvail = new int[26];
		setCharsAvail(charsAvail);
		gemSlotType = gemType;
	}

	/**
	 * @return The name of the <b>Item</b>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param newName
	 *            The new name of the <b>Item</b>.
	 */
	public void setName(String newName) {
		name = newName;
	}

	/**
	 * @return The graphical representation of the <b>Item</b>.
	 */
	public Image drawItem() {
		return drawing;
	}

	/**
	 * @return An array of available characters as specified by
	 *         {@link Item#charsAvail}.
	 */
	public int[] getCharsAvail() {
		return charsAvail;
	}

	/**
	 * Amount of a character at the given position as specified by
	 * {@link Item#charsAvail}.
	 * 
	 * @param pos
	 *            Position in the {@link Item#charsAvail} array.
	 * @return The number of that specific character provided.
	 */
	public int getCharAvail(int pos) {
		return charsAvail[pos];
	}

	/**
	 * Amount of a specified character.
	 * 
	 * @param which
	 *            The character being searched for.
	 * @return The number of that specific character provided.
	 */
	public int getCharAvail(char which) {
		which = String.valueOf(which).toLowerCase().charAt(0);
		return getCharAvail(which - 'A');
	}

	/**
	 * Change the characters provided by this <b>Item</b>.
	 * 
	 * @param newValue
	 *            An array of integers as specified by {@link Item#charsAvail}.
	 */
	public void setCharsAvail(int[] newValue) {
		if (newValue == null)
			return;
		for (int i = 0; i < 26; i++) {
			charsAvail[i] = newValue[i];
		}
	}

	/**
	 * Change the amount of a character at a specific position provided.
	 * Position is used as defined in {@link Item#charsAvail}.
	 * 
	 * @param pos
	 *            Position in the {@link Item#charsAvail} array.
	 * @param value
	 *            New value at the position.
	 */
	public void setCharAvail(int pos, int value) {
		charsAvail[pos] = value;
	}

	/**
	 * Change the amount of a specific character provided.
	 * 
	 * @param which
	 *            Character who's value needs to be changed.
	 * @param value
	 *            New value of that character.
	 */
	public void setCharAvail(char which, int value) {
		which = String.valueOf(which).toLowerCase().charAt(0);
		setCharAvail(which - 'a', value);
	}

	/**
	 * @return The stat of this <b>Item</b> regardless of type.
	 */
	public int getStat() {
		return stat;
	}

	/**
	 * Change the value of the stat of this <b>Item</b> regardless of type.
	 * 
	 * @param newVal
	 *            New value of stat.
	 */
	public void setStat(int newVal) {
		stat = newVal;
	}

	/**
	 * @return The type of the <b>Item</b>, as specified by {@link Item#weapon}.
	 */
	public Boolean getType() {
		return weapon;
	}

	/**
	 * @return The description of the <b>Item</b>.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the letter at this position.
	 * 
	 * @param pos
	 *            the position of the letter required.
	 * @return The letter at pos.
	 */
	public static char getLetter(int pos) {
		return letters[pos];
	}

	/**
	 * Initializes an array with all the letters required, and randomizes it.
	 * 
	 * @return An array containing all letters of the sentence "LAUGHTER BY
	 *         WORD", in a random order.
	 */
	private static char[] initLetters() {
		char[] out = "LAUGHTERBYWOD".toCharArray();

		for (int i = 0; i < out.length; i++) {
			int temp = (int) (Math.random() * out.length);
			char tempCh = out[temp];
			out[temp] = out[i];
			out[i] = tempCh;
		}

		return out;
	}

	/**
	 * Reinitializes all static fields.
	 */
	public static void reset() {
		allItems = initAllItems();
		letters = initLetters();
	}

	/**
	 * Returns a random <b>Item</b> from the level, and removes it.
	 * 
	 * @param level
	 *            the level from which the <b>Item</b> is needed.
	 * @return The <b>Item</b> chosen.
	 */
	public static Item getItem(int level) {
		return allItems[level].remove((int) (Math.random() * allItems[level].size()));
	}

	/**
	 * The amount of unused <b>Item</b>s on this level.
	 * 
	 * @param level
	 *            the level for which remaining <b>Item</b>s should be checked.
	 * @return The amount of unused <b>Item</b>s on this level.
	 */
	public static int itemsLeft(int level) {
		return allItems[level].size();
	}

	/**
	 * Initializes all <b>Item</b>s available in the game.
	 * 
	 * @return All <b>Item</b>s available in the game as an array of
	 *         {@link ArrayList}, where a given {@link ArrayList} is at the
	 *         specified level position.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<Item>[] initAllItems() {
		ArrayList<Item>[] out;
		Scanner in;
		File[] folder;

		out = (ArrayList<Item>[]) new ArrayList[4];
		for (int i = 0; i < out.length; i++) {
			out[i] = new ArrayList<Item>();
		}

		try {
			folder = new File("resources/itemStats/regular").listFiles();
			for (File file : folder) {
				in = new Scanner(file);
				String name = file.getName().substring(0, file.getName().indexOf('.'));
				Boolean type = in.nextLine().equals("w");
				int stat = Integer.parseInt(in.nextLine());
				String description = in.nextLine();
				int[] chars = new int[26];
				for (int i = 0; i < 26; i++) {
					chars[i] = in.nextInt();
				}
				out[Math.min(stat / 30, 2)]
						.add(new Item(name, type, stat, description, chars, (Math.random() < 0.5 ? 1 : -1)));
			}

			folder = new File("resources/itemStats/epic").listFiles();
			for (File file : folder) {
				in = new Scanner(file);
				String name = file.getName().substring(0, file.getName().indexOf('.'));
				Boolean type = in.nextLine().equals("w");
				int stat = Integer.parseInt(in.nextLine());
				String description = in.nextLine();
				int[] chars = new int[26];
				for (int i = 0; i < 26; i++) {
					chars[i] = in.nextInt();
				}
				out[3].add(new Item(name, type, stat, description, chars, (Math.random() < 0.5 ? 1 : -1)));
			}
		} catch (IOException ioe) {
		}

		out[2].add(new Item("Torch", null, 0, "Increases your vision by 1 in dark rooms.", null, 0));
		return out;
	}

	/**
	 * @return The type of the {@link items.GemInventory.Gem gem} slot as
	 *         specified by {@link Item#gemSlotType}.
	 */
	public int getGemSlotType() {
		return gemSlotType;
	}
}
