package frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import characters.Enemy;
import characters.MainCharacter;
import items.GemInventory;
import items.Item;
import items.SpellInventory;
import mapping.Tile;
import mapping.TileMap;
import mapping.Visibility;

/**
 * <b>GameGraphics</b> merges all the {@link mapping.TileMap maps} used into the
 * game into one location, which allows interaction to be done between the
 * {@link characters.MainCharacter player} and the terrain.
 * 
 * It provides rendering capabilities for both movement and battle game-play, by
 * storing information about both.
 * <p>
 * Changes: The class now supports all aspects of combat, all three inventories,
 * and victory conditions.
 * <p>
 * Total Time Spent: 10 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class GameGraphics {
	/**
	 * All the words in the English dictionary provided by SIL
	 */
	private final static HashSet<String> DICTIONARY = initDic();

	/**
	 * Stores all the {@link mapping.TileMap maps} in the game, and provides
	 * access to them through their name.
	 */
	private HashMap<String, TileMap> game;

	/**
	 * The {@link characters.MainCharacter character} that is accessible to the
	 * user.
	 */
	private MainCharacter player;

	/**
	 * The position of the {@link characters.MainCharacter character} on the
	 * current {@link mapping.TileMap map}.
	 */
	private Point playerPos;

	/**
	 * The name of the current {@link mapping.TileMap map}, as defined by the
	 * .map files.
	 */
	private String curMap;

	/**
	 * The current {@link characters.Enemy enemy} being contested in a battle.
	 */
	private Enemy enemy;

	/**
	 * The words the user has written in the current battle.
	 */
	private ArrayList<String> damageWords;

	/**
	 * The word the user is fighting against.
	 */
	private String damageWord;

	/**
	 * In combat, this array stores which characters from the
	 * ({@link items.ItemSet items} have already been used.
	 */
	private int[] charsUsed;

	/**
	 * The amount of points this game-play has earned, used for high score.
	 */
	private int points;

	/**
	 * Used to provide the {@link characters.MainCharacter player} with the
	 * first key.
	 */
	private boolean firstKey;

	/**
	 * A constructor to load a new game.
	 */
	public GameGraphics() {
		game = new HashMap<String, TileMap>();

		Tile.reset();
		Item.reset();

		player = null;

		points = 0;

		do {
			try {
				for (File map : new File("resources/maps").listFiles()) {
					String name = map.getName();
					name = name.substring(0, name.indexOf('.'));
					game.put(name, new TileMap(name));
				}

				curMap = "k1";
				player = new MainCharacter();
				playerPos = new Point(5, 3);
				firstKey = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (player == null);
	}

	/**
	 * Attempts to move the {@link characters.MainCharacter player}, and returns
	 * the resulting message.
	 * 
	 * @param dir
	 *            a point to represent the direction of movement, with dir.x
	 *            being the change in x, and dir.y being the change in y.
	 * @return The message of the tile that the {@link characters.MainCharacter
	 *         player} moved into, or "" if combat needs to take place.
	 */
	public String move(Point dir) {
		if (dir == null) {
			return null;
		}

		Point newLoc = new Point(playerPos.x + dir.x, playerPos.y + dir.y);

		if (curMap.equals("m15") && Math.random() < 0.25) {
			newLoc = new Point(playerPos.x + dir.x * -1, playerPos.y + dir.y * -1);
		}

		player.setDir(dir);

		String message;

		if (newLoc.x == -1 || newLoc.x == 11 || newLoc.y == -1 || newLoc.y == 11) {
			return doorMove(dir);
		} else {
			message = game.get(curMap).getTile(newLoc).getMessage();
			if (game.get(curMap).getTile(newLoc).isPassable()) {
				playerPos = new Point(newLoc);
				double chance = game.get(curMap).getTile(playerPos).getEnemyChance();
				double compare = Math.random();
				if (message != null && message.equals("I have found a special item!")) {
					game.get(curMap).setTileEmpty(newLoc, getLevel());
					player.addItem(Item.getItem(3));
				} else if (message != null && message.equals("I have found a letter!")) {
					player.getInventory().addLetter((char) (chance));
					game.get(curMap).setTileEmpty(newLoc, getLevel());
				} else if (chance != 1.0 && compare < chance) {
					return "";
				} else if (chance == 1.0) {
					game.get(curMap).setTileEmpty(newLoc, getLevel());
				}
			} else if (message != null && message.equals("The door is locked. I need to find a key to open it!")) {
				if (player.getInventory().getKey()) {
					game.get(curMap).unlockDoor(newLoc, getLevel());
					playerPos = new Point(newLoc);
					return null;
				}
			} else if (message != null && message.equals("BRING ME BACK MY LOST LETTERS!")) {
				if (player.getInventory().getLetters().length() == 13) {
					message = "You win!" + points;
				} else {
					message += " You still need " + (13 - player.getInventory().getLetters().length()) + " letter"
							+ ((13 - player.getInventory().getLetters().length()) == 1 ? "." : "s.");
					if (firstKey) {
						awardKey();
						firstKey = false;
					}
				}
			}
		}

		return message;
	}

	/**
	 * Movement through a door, which requires a change of
	 * {@link mapping.TileMap map} rather than position.
	 * 
	 * @param dir
	 *            a point to represent the direction of movement, with dir.x
	 *            being the change in x, and dir.y being the change in y.
	 * @return String represented by null, used for the return of the message
	 *         from {@link GameGraphics#move(Point)}.
	 */
	private String doorMove(Point dir) {
		if (curMap.equals("k7") && dir.y == 1) {
			curMap = "k11";
			playerPos.y = 1;
			GameMusic.endMusic();
			GameMusic.playSound(2);
			return "I exit the temple into a forest!";
		} else if (curMap.equals("k11") && dir.y == -1) {
			curMap = "k7";
			playerPos.y = 9;
			GameMusic.endMusic();
			GameMusic.playSound(1);
			return "I leave the clearing and enter the temple of Shutka!";
		} else if (curMap.equals("k13") && dir.x == -1) {
			curMap = "s5";
			playerPos = new Point(5, 9);
			GameMusic.endMusic();
			GameMusic.playSound(3);
			return "I descend into ancient mystical caves!";
		} else if (curMap.equals("s5") && dir.y == 1) {
			curMap = "k13";
			playerPos = new Point(1, 1);
			GameMusic.endMusic();
			GameMusic.playSound(2);
			return "I climb back up, into the forest!";
		} else if (curMap.equals("q1") && dir.x == 1) {
			curMap = "g1";
			playerPos = new Point(1, 9);
			GameMusic.endMusic();
			GameMusic.playSound(1);
			return "I travel through the secret passage into the temple of Shutka!";
		} else if ((curMap.equals("g11") || curMap.equals("i13") || curMap.equals("i15") || curMap.equals("k15"))
				&& Math.random() < 0.5) {
			String[] possibilities = { "g11", "i13", "i15", "k15" };
			curMap = possibilities[(int) (Math.random() * 4)];
			playerPos = new Point(2, 5);
			return "I got lost, and arrived at a random intersection!";
		} else {
			if (dir.x == 1) {
				curMap = (char) (curMap.charAt(0) + 2) + curMap.substring(1);
				playerPos.x = 1;
			} else if (dir.x == -1) {
				curMap = (char) (curMap.charAt(0) - 2) + curMap.substring(1);
				playerPos.x = 9;
			} else if (dir.y == 1) {
				curMap = curMap.substring(0, 1) + (Integer.parseInt(curMap.substring(1)) + 2);
				playerPos.y = 1;
			} else if (dir.y == -1) {
				curMap = curMap.substring(0, 1) + (Integer.parseInt(curMap.substring(1)) - 2);
				playerPos.y = 9;
			}
			return null;
		}
	}

	/**
	 * This method allows the current {@link mapping.TileMap map} to be
	 * displayed. It takes into account the {@link mapping.Visibility
	 * visibility} available to the {@link characters.MainCharacter player}.
	 * 
	 * @return An {@link java.awt.Image image} representing the current
	 *         {@link mapping.TileMap map}, with invisible {@link mapping.Tile
	 *         tiles} being black.
	 */
	public Image getMap() {
		Image out = game.get(curMap).drawMap();

		Visibility curVis = game.get(curMap).getTile(playerPos).getVisibility();
		Image character = (curVis == Visibility.ALL_BUT_CURRENT ? getPartialCharacter() : player.drawCharacter());

		Graphics g = out.getGraphics();
		g.drawImage(character, playerPos.x * Tile.TILE_SIZE, playerPos.y * Tile.TILE_SIZE, null);

		if (getLevel() == 3 && (!curMap.equals("o5") && !curMap.equals("q5") && !curMap.equals("q3"))) {
			int vision = player.getInventory().increaseInVision();
			g.setColor(new Color(0, 0, 0));
			for (int x = 0; x < 11; x++) {
				for (int y = 0; y < 11; y++) {
					if (!((playerPos.x - vision <= x && playerPos.x + vision >= x)
							&& (playerPos.y - vision <= y && playerPos.y + vision >= y))) {
						g.fillRect(Tile.TILE_SIZE * x, Tile.TILE_SIZE * y, Tile.TILE_SIZE, Tile.TILE_SIZE);
					}
				}
			}
		}

		return out;
	}

	/**
	 * When the {@link characters.MainCharacter character} is standing on a
	 * door, he is replaced with a red dot, provided by this method.
	 * 
	 * @return A red oval used to display the {@link characters.MainCharacter
	 *         character} on a door.
	 */
	private Image getPartialCharacter() {
		Image out = new BufferedImage(Tile.TILE_SIZE, Tile.TILE_SIZE, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < Tile.TILE_SIZE; i++)
			for (int j = 0; j < Tile.TILE_SIZE; j++)
				((BufferedImage) out).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
		Graphics g = out.getGraphics();
		g.setColor(new Color(255, 0, 0, 255));
		g.fillOval(15, 15, Tile.TILE_SIZE - 30, Tile.TILE_SIZE - 30);
		return out;
	}

	/**
	 * Allows the {@link java.awt.Image image} of the currently equipped
	 * {@link items.ItemSet items} to be visible.
	 * 
	 * @return An {@link java.awt.Image image} containing the currently equipped
	 *         weapon and shield.
	 */
	public Image getItemSetImage() {
		Image out = new BufferedImage(250, 550, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < out.getWidth(null); i++)
			for (int j = 0; j < out.getHeight(null); j++)
				((BufferedImage) out).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
		Graphics g = out.getGraphics();
		Item weapon = player.getItemSet().getWeapon();
		Item shield = player.getItemSet().getShield();
		GemInventory.Gem[] gems = player.getEquipedGems();

		g.setFont(new Font("Calibri", Font.PLAIN, 18));

		if (weapon != null) {
			g.drawImage(weapon.drawItem(), 27, 95, null);
			if (weapon.getGemSlotType() < 0)
				g.setColor(new Color(255, 0, 0));
			else
				g.setColor(new Color(0, 255, 0));
			g.drawRect(35, 58, 33, 33);

			g.setColor(new Color(255, 255, 255, 255));
			g.drawString(weapon.getName() + " - " + weapon.getStat(), 27, 215);
		}

		if (shield != null) {
			g.drawImage(shield.drawItem(), 133, 95, null);
			if (shield.getGemSlotType() < 0)
				g.setColor(new Color(255, 0, 0));
			else
				g.setColor(new Color(0, 255, 0));
			g.drawOval(141, 58, 33, 33);
			g.setColor(new Color(255, 255, 255, 255));
			g.drawString(shield.getName() + " - " + shield.getStat() + "(" + shield.getStat() / 6 + "s)", 27, 235);
		}

		g.setColor(new Color(255, 255, 255, 255));
		if (gems[0] != null) {
			g.drawImage(gems[0].drawGem(), 32, 55, null);
			g.drawString("Value: " + Math.abs(gems[0].getValue()), 32, 50);
		}

		if (gems[1] != null) {
			g.drawImage(gems[1].drawGem(), 138, 55, null);
			g.drawString("Value: " + Math.abs(gems[1].getValue()), 138, 50);
		}

		for (int i = 0; i < 26; i++) {
			g.drawString((char) ('A' + i) + " - " + player.getItemSet().getCharAvail((char) (i + 'A')),
					27 + i / 13 * 50, 255 + (i % 13) * 20);
		}

		return out;
	}

	/**
	 * Allows the {@link java.awt.Image image} of the {@link items.Item items}
	 * currently in the {@link items.ItemInventory inventory} to be visible.
	 * 
	 * @return An {@link java.awt.Image image} containing all unequipped
	 *         {@link items.Item items}.
	 */
	public Image getItemInventory() {
		Image out = new BufferedImage(550, 250, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < out.getWidth(null); i++)
			for (int j = 0; j < out.getHeight(null); j++)
				((BufferedImage) out).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
		ArrayList<Item> items = player.getInventory().getItems();
		Graphics g = out.getGraphics();
		int keys = 0;

		for (int i = 0; i < items.size(); i++) {
			g.drawImage(items.get(i).drawItem(), 50 * (i % 11), 150 * (i / 11), null);
		}

		g.setFont(new Font("Calibri", Font.PLAIN, 18));
		g.setColor(new Color(255, 255, 255, 255));
		g.drawString("You have " + player.getInventory().getKeys() + " key" + (keys == 1 ? "" : "s") + "."
				+ (player.getInventory().getTorch() ? " You have a torch." : ""), 0, 125);
		g.drawString("You have the letters: " + player.getInventory().getLetters(), 0, 145);

		return out;
	}

	/**
	 * Allows the {@link java.awt.Image image} of the currently available
	 * {@link items.GemInventory.Gem gems} to be visible.
	 * 
	 * @return An {@link java.awt.Image image} containing the unequipped
	 *         {@link items.GemInventory.Gem gems}.
	 */
	public Image getGemInventory() {
		Image out = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < out.getWidth(null); i++)
			for (int j = 0; j < out.getHeight(null); j++)
				((BufferedImage) out).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
		ArrayList<GemInventory.Gem> gems = player.getGems().getGems();
		Graphics g = out.getGraphics();

		for (int i = 0; i < gems.size(); i++) {
			g.drawImage(gems.get(i).drawGem(), 40 * (i % 3), 40 * (i / 3), null);
		}

		return out;
	}

	/**
	 * Allows the {@link java.awt.Image image} of the
	 * {@link items.SpellInventory currently available spells} to be visible.
	 * 
	 * @return An {@link java.awt.Image image} containing the unused spells.
	 */
	public Image getSpellInventory() {
		Image out = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < out.getWidth(null); i++)
			for (int j = 0; j < out.getHeight(null); j++)
				((BufferedImage) out).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
		ArrayList<Color> spells = player.getSpells().getSpells();
		Graphics g = out.getGraphics();

		for (int i = 0; i < spells.size(); i++) {
			g.drawImage(SpellInventory.drawSpell(spells.get(i)), 40 * (i % 3), 40 * (i / 3), null);
		}

		return out;
	}

	/**
	 * Creates a box presenting information about the {@link items.Item item}
	 * currently being scrolled-over.
	 * 
	 * @param pos
	 *            the position of the item being scrolled over.
	 * @return An {@link java.awt.Image image} of a box with information about
	 *         the {@link items.Item item}.
	 */
	public Image getItemDescription(int pos) {
		Image out = new BufferedImage(250, 100, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < out.getWidth(null); i++)
			for (int j = 0; j < out.getHeight(null); j++)
				((BufferedImage) out).setRGB(i, j, new Color(50, 50, 50, 255).getRGB());
		if (player.getInventory().getItems().size() > pos) {
			Graphics g = out.getGraphics();
			Item m = player.getInventory().getItems().get(pos);
			String desc = m.getDescription();
			int splitPoint = desc.substring(0, desc.length() / 2).lastIndexOf(' ');

			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));

			g.drawString((m.getType() ? "Weapon" : "Shield") + " - " + m.getName(), 5, 25);
			g.drawString((m.getType() ? "Damage" : "Defence") + ": " + m.getStat() + "     Gem Slot Type: "
					+ (m.getGemSlotType() < 0 ? "-" : "+"), 5, 45);
			g.setFont(new Font("Comic Sans MS", Font.PLAIN, 9));
			g.drawString(desc.substring(0, splitPoint), 5, 60);
			g.drawString(desc.substring(splitPoint + 1), 5, 75);
			String temp1 = "";
			String temp2 = "";
			for (int i = 0; i < 13; i++) {
				temp1 += (char) (i + 'A') + ":" + m.getCharAvail(i) + " ";
				temp2 += (char) (i + 'A' + 13) + ":" + m.getCharAvail(i + 13) + " ";
			}
			g.drawString(temp1, 5, 90);
			g.drawString(temp2, 5, 100);
		}

		return out;
	}

	/**
	 * Creates a box that shows the value of the {@link items.GemInventory.Gem
	 * gem} being hovered over.
	 * 
	 * @param pos
	 *            the position of the {@link items.GemInventory.Gem gem} being
	 *            hovered over.
	 * @return The value of the {@link items.GemInventory.Gem gem} being hovered
	 *         over.
	 */
	public Image getGemDescription(int pos) {
		Image out = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < out.getWidth(null); i++)
			for (int j = 0; j < out.getHeight(null); j++)
				((BufferedImage) out).setRGB(i, j, new Color(50, 50, 50, 255).getRGB());

		if (player.getGems().getGems().size() > pos) {
			Graphics g = out.getGraphics();
			GemInventory.Gem m = player.getGems().getGems().get(pos);

			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

			g.drawString("" + Math.abs(m.getValue()), 5, out.getHeight(null) - 5);
		}

		return out;
	}

	/**
	 * @return The level associated with the location of the current
	 *         {@link mapping.TileMap map}.
	 */
	public int getLevel() {
		if (Integer.parseInt(curMap.substring(1)) >= 11) {
			return 2;
		} else if ("opqrstu".contains(curMap.substring(0, 1))) {
			return 3;
		} else {
			return 1;
		}
	}

	/**
	 * Switches the equipped {@link items.Item item} with the one at pos.
	 * 
	 * @param pos
	 *            the position of the {@link items.Item item} to be switched
	 *            with.
	 */
	public void switchItem(int pos) {
		player.switchItems(pos);
	}

	/**
	 * Moves the equipped {@link items.Item item} into the
	 * {@link items.ItemInventory inventory}.
	 * 
	 * @param weapon
	 *            true if weapon was selected, false if shield was selected.
	 */
	public void unequipItem(boolean weapon) {
		player.unequip(weapon);
	}

	/**
	 * Equips the {@link items.GemInventory.Gem gem} at pos.
	 * 
	 * @param pos
	 *            the position of the {@link items.GemInventory.Gem gem} being
	 *            equipped.
	 */
	public void setGem(int pos) {
		player.setGem(pos);
	}

	/**
	 * Swaps the spell at pos with the last one.
	 * 
	 * @param pos
	 *            the position of the spell being swapped with the last one.
	 */
	public void swapSpells(int pos) {
		player.swapSpells(pos);
	}

	public void useSpell(int pos) {
		int damage = enemy.compareColor(player.useSpell(pos));
		enemy.takeDamage(damage);
	}

	/**
	 * @return How long a battle should take place based on the defense of the
	 *         equipped shield and {@link items.GemInventory.Gem gem}.
	 */
	public int getBattleLength() {
		return player.getItemSet().getDefense() + (player.getEquipedGems()[1] == null ? 0
				: player.getEquipedGems()[1].getValue())
				* (player.getItemSet().getShield() == null ? 0 : player.getItemSet().getShield().getGemSlotType());
	}

	/**
	 * Initializes all variables related to a regular battle.
	 */
	public void initiateBattle() {
		damageWords = new ArrayList<String>();
		damageWords.add("");
		do {
			damageWord = (String) DICTIONARY.toArray()[(int) (Math.random() * DICTIONARY.size())];
		} while (damageWord.length() < 5);
		charsUsed = new int[26];
		enemy = new Enemy(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()),
				player.getItemSet().getDamage() * (1 + getLevel()) + (int) (Math.random() * Math.pow(5, getLevel())),
				Enemy.NAMES[getLevel() - 1][(int) (Math.random() * Enemy.NAMES[getLevel() - 1].length)]);
	}

	/**
	 * Initializes all variables related to a boss battle.
	 */
	public void initiateBossBattle() {
		initiateBattle();
		enemy = new Enemy(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()),
				player.getItemSet().getDamage() * 2 + getLevel() * 30 + (int) (Math.random() * 40),
				Enemy.NAMES[3][getLevel() - 1]);
	}

	/**
	 * This method is used at the end of a battle to see if the
	 * {@link characters.MainCharacter player} survived the battle. It
	 * increments {@link GameGraphics#points points} by all uneccessary damage.
	 * 
	 * @return True if {@link characters.MainCharacter player} won the battle,
	 *         false if the {@link characters.MainCharacter player} lost.
	 */
	public boolean endBattle() {
		points += calculateDamage() - enemy.getHP();
		return calculateDamage() > enemy.getHP();
	}

	/**
	 * This method calculates the amount of damage the
	 * {@link characters.MainCharacter player} had dealt so far.
	 * 
	 * @return Damage dealt by the {@link characters.MainCharacter player} in
	 *         the current battle.
	 */
	private int calculateDamage() {
		HashSet<String> uniqueWords = new HashSet<String>();
		for (String word : damageWords.subList(0, damageWords.size() - 1)) {
			if (!word.equals(damageWord) && !word.equals(""))
				uniqueWords.add(word);
		}
		int weaponDamage = player.getItemSet().getDamage() + (player.getEquipedGems()[0] == null ? 0
				: player.getEquipedGems()[0].getValue())
				* (player.getItemSet().getWeapon() == null ? 0 : player.getItemSet().getWeapon().getGemSlotType());
		int damage = 0;

		for (String word : uniqueWords) {
			int substrLen = Math.max(2, longestSubstr(damageWord, word));
			int similiarLetters = commonLetters(damageWord, word);
			if (DICTIONARY.contains(word)) {
				if (substrLen >= 3) {
					damage += substrLen * 2 + weaponDamage * 2;
				} else if (similiarLetters >= 3) {
					damage += similiarLetters + weaponDamage;
				}
			} else {
				damage -= weaponDamage * 2 + 5;
			}
		}
		return damage;
	}

	/**
	 * Gives the {@link characters.MainCharacter player} an {@link items.Item
	 * item}, unless there are no {@link items.Item items} left in this level.
	 */
	public void awardItem() {
		if (Item.itemsLeft(getLevel() - 1) != 0)
			player.addItem(Item.getItem(getLevel() - 1));
		else
			awardKey();
	}

	/**
	 * Gives the {@link characters.MainCharacter player} a key, or a
	 * {@link items.GemInventory.Gem gem}, or a spell. The
	 * {@link items.GemInventory.Gem gem} is only available on level 2 and 3,
	 * and the spell is only available on level 3.
	 */
	public void awardKey() {
		if (Math.random() < 0.5 && (getLevel() == 2 || getLevel() == 3))
			awardGem();
		else if (Math.random() < 0.5 && getLevel() == 3)
			awardSpell();
		else
			player.addItem(new Item("Key", null, 0, "This key can open any locked door!", null, 0));
	}

	/**
	 * Gives the {@link characters.MainCharacter player} a
	 * {@link items.GemInventory.Gem gem}.
	 */
	public void awardGem() {
		player.addGem();
	}

	/**
	 * Gives the {@link characters.MainCharacter player} a spell.
	 */
	public void awardSpell() {
		player.addSpell();
	}

	/**
	 * @return An {@link java.awt.Image image} of the current
	 *         {@link characters.Enemy foe}, with the name of the
	 *         {@link characters.Enemy foe}, and the word to fight against.
	 */
	public Image getEnemy() {
		Image out = new BufferedImage(550, 550, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < out.getWidth(null); i++)
			for (int j = 0; j < out.getHeight(null); j++)
				((BufferedImage) out).setRGB(i, j, new Color(0, 0, 0, 0).getRGB());
		Graphics g = out.getGraphics();
		g.drawImage(enemy.drawEnemy(), 25, 25, null);
		g.setFont(new Font("Bauhaus 93", Font.PLAIN, 45));
		g.setColor(new Color(255, 255, 255));
		g.drawString(enemy.getName(), (550 - g.getFontMetrics().stringWidth(enemy.getName())) / 2, 35);
		g.drawString(enemy.getHP() + " VS " + calculateDamage(),
				(550 - g.getFontMetrics().stringWidth(enemy.getHP() + " VS " + calculateDamage())) / 2, 70);
		g.setFont(new Font("Symbola", Font.PLAIN, 50));
		g.setColor(new Color(0, 0, 0));
		g.fillRect((550 - g.getFontMetrics().stringWidth(damageWord)) / 2, 480,
				g.getFontMetrics().stringWidth(damageWord), 60);
		g.setColor(new Color(255, 255, 255));
		g.drawString(damageWord, (550 - g.getFontMetrics().stringWidth(damageWord)) / 2, 535);
		return out;
	}

	/**
	 * @return The word the user is currently inputing.
	 */
	public String getCurWord() {
		return damageWords.get(damageWords.size() - 1);
	}

	/**
	 * This method controls addition of letters to the end of the current word.
	 * 
	 * @param letter
	 *            the letter that the user is attempting to add.
	 * @return Whether the letter was successfully added.
	 */
	public boolean incrementCurWord(char letter) {
		if (damageWords.get(damageWords.size() - 1).length() >= 13
				|| charsUsed[letter - 'A'] >= player.getItemSet().getCharAvail(letter))
			return false;
		damageWords.set(damageWords.size() - 1, damageWords.get(damageWords.size() - 1) + letter);
		charsUsed[letter - 'A']++;
		return true;
	}

	/**
	 * Allows the user to add the current word to damaging words, and start a
	 * new word.
	 */
	public void finalizeCurWord() {
		damageWords.add("");
	}

	/**
	 * Clears the current word, allowing the user to start on a new word.
	 */
	public void clearCurWord() {
		for (int i = 0; i < damageWords.get(damageWords.size() - 1).length(); i++)
			charsUsed[damageWords.get(damageWords.size() - 1).charAt(i) - 'A']--;
		damageWords.set(damageWords.size() - 1, "");
	}

	public int getAvailable(char which) {
		return player.getItemSet().getCharAvail(which) - charsUsed[which - 'A'];
	}

	/**
	 * Finds the length of the longest substring in the two words.
	 * 
	 * @param first
	 *            the first String.
	 * @param second
	 *            the second String.
	 * @return The length of the longest common substring.
	 */
	private static int longestSubstr(String first, String second) {
		int maxLen = 0;
		int fl = first.length();
		int sl = second.length();
		int[][] table = new int[fl + 1][sl + 1];

		for (int i = 1; i <= fl; i++) {
			for (int j = 1; j <= sl; j++) {
				if (first.charAt(i - 1) == second.charAt(j - 1)) {
					table[i][j] = table[i - 1][j - 1] + 1;
					if (table[i][j] > maxLen)
						maxLen = table[i][j];
				}
			}
		}
		return maxLen;
	}

	/**
	 * Finds the amount of letters in common between two Strings.
	 * 
	 * @param first
	 *            the first String.
	 * @param second
	 *            the second String.
	 * @return The amount of common letters between the two Strings.
	 */
	private static int commonLetters(String first, String second) {
		int out = 0;
		int[] lettersFirst = new int[26];
		int[] lettersSecond = new int[26];

		for (int i = 0; i < 26; i++) {
			lettersFirst[i] = 0;
			lettersSecond[i] = 0;
		}

		for (int i = 0; i < first.length(); i++) {
			lettersFirst[first.charAt(i) - 65]++;
		}

		for (int i = 0; i < second.length(); i++) {
			lettersSecond[second.charAt(i) - 65]++;
		}

		for (int i = 0; i < 26; i++) {
			out += Math.min(lettersFirst[i], lettersSecond[i]);
		}

		return out;
	}

	private static HashSet<String> initDic() {
		HashSet<String> out = new HashSet<String>();
		Scanner in;

		try {
			in = new Scanner(new File("resources/miscellaneous/wordsEN.txt"));

			while (in.hasNextLine()) {
				String temp = in.nextLine();
				if (temp.length() >= 3 && temp.length() <= 13)
					out.add(temp.toUpperCase());
			}

			in.close();
		} catch (FileNotFoundException e) {
		}

		return out;
	}
}
