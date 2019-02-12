package mapping;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import items.Item;

/**
 * The <b>Tile</b> class is the most basic unit of mapping.
 * <p>
 * Each instance of the <b>Tile</b> class represents one tile on a
 * {@link TileMap}. It contains the information on how to draw it, as well as
 * whether it is passable, and how the {@link characters.MainCharacter
 * character} looks on it. It should not be used without a {@link TileMap}.
 * <p>
 * Changes: The class now only contains required fields, and properly sets the
 * messages for <b>Tile</b>s.
 * <p>
 * Total Time Spent: 1.5 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 * 
 * @see TileMap
 *
 */
public class Tile {
	/**
	 * The width of one <b>Tile</b>.
	 */
	public static final int TILE_SIZE = 50;

	/**
	 * The position of the current letter used.
	 */
	private static int letterNum = 0;

	/**
	 * The position of the current statue part used.
	 */
	private static int statueNum = 0;

	/**
	 * Stores whether this <b>Tile</b> can contain
	 * {@link characters.MainCharacter MainCharacter} or not.
	 */
	private boolean passable;

	/**
	 * The {@link Image} that is used to draw this <b>Tile</b>.
	 */
	private Image drawing;

	/**
	 * The {@link Visibility visibility} provided when standing on this
	 * <b>Tile</b>.
	 */
	private Visibility visibility;

	/**
	 * The chance of encountering an {@link characters.Enemy enemy} on this
	 * <b>Tile</b>. 0 is none and 1 is guaranteed. Must be between 0 and 1.
	 */
	private double enemyChance;

	/**
	 * The message to be displayed when this <b>Tile</b> is encountered.
	 */
	private String message;

	/**
	 * Create the <b>Tile</b> from given options.
	 * 
	 * @param type
	 *            the type of the <b>Tile</b>.
	 * @param level
	 *            the current level, as defined in
	 *            {@link frame.GameGraphics#getLevel() GameGraphics.getLevel()}.
	 */
	public Tile(char type, int level) {
		try {
			switch (type) {
			case ' ':
				passable = true;
				drawing = ImageIO.read(new File("resources/graphics/textures/floor" + level + ".png"));
				visibility = Visibility.ALL;
				enemyChance = 0.05 * level;
				message = null;
				break;
			case 'w':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/wall" + level + ".png"));
				enemyChance = 0.0;
				message = "I cannot walk through walls.";
				break;
			case 's':
				passable = true;
				visibility = Visibility.ALL_BUT_CURRENT;
				drawing = ImageIO.read(new File("resources/graphics/textures/wall" + level + ".png"));
				enemyChance = 0.0;
				message = "I've discovered a secret door.";
				break;
			case 'd':
				passable = true;
				visibility = Visibility.ALL_BUT_CURRENT;
				drawing = ImageIO.read(new File("resources/graphics/textures/door" + level + ".png"));
				enemyChance = 0.0;
				message = null;
				break;
			case 'c':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/door" + level + ".png"));
				enemyChance = 0.0;
				message = "The door is locked. I need to find a key to open it!";
				break;
			case 'p':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/pillar.png"));
				enemyChance = 0.0;
				message = "A beautiful column stands in front of me.";
				break;
			case 'b':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/boulder.png"));
				enemyChance = 0.0;
				message = "I ran into a boulder. I should watch where I'm going!";
				break;
			case 'g':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/glowingwall.png"));
				enemyChance = 0.0;
				message = "The wall sparkles with different colours.";
				break;
			case 't':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/torch.png"));
				enemyChance = 0.0;
				message = "A torch lights the room.";
				break;
			case 'l':
				passable = true;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/floor" + level + ".png"));
				drawing.getGraphics().drawImage(
						ImageIO.read(new File("resources/graphics/textures/" + Item.getLetter(letterNum) + ".png")), 0,
						0, null);
				enemyChance = Item.getLetter(letterNum++);
				message = "I have found a letter!";
				break;
			case 'h':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/hut.png"));
				enemyChance = 0.0;
				message = "The villagers look at me in fear as I pass by their hut.";
				break;
			case 'X':
				passable = false;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/statue" + statueNum++ + ".png"));
				enemyChance = 0.0;
				message = "BRING ME BACK MY LOST LETTERS!";
				break;
			case 'S':
			case 'N':
			case 'W':
			case 'E':
				passable = true;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/stairs" + type + ".png"));
				enemyChance = 0.0;
				message = null;
				break;
			case 'B':
				passable = true;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/floor" + level + ".png"));
				drawing.getGraphics().drawImage(
						ImageIO.read(new File("resources/graphics/textures/boss" + level + "icon.png")), 0, 0, null);
				enemyChance = 1.0;
				message = "I have encountered a monster unlike any I've seen before!";
				break;
			case 'i':
				passable = true;
				visibility = Visibility.ALL;
				drawing = ImageIO.read(new File("resources/graphics/textures/floor" + level + ".png"));
				drawing.getGraphics().drawImage(ImageIO.read(new File("resources/graphics/textures/itemicon.png")), 0,
						0, null);
				enemyChance = 0.0;
				message = "I have found a special item!";
				break;
			default:
				passable = false;
				visibility = Visibility.ALL;
				drawing = null;
				enemyChance = 0.0;
				message = null;
				break;
			}
			if (drawing == null) {
				System.out.println("No image for" + type);
			}
		} catch (IOException e) {
			try {
				drawing = ImageIO.read(new File("resources/graphics/characters/main/temp.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @return The passability of this <b>Tile</b>.
	 */
	public boolean isPassable() {
		return passable;
	}

	/**
	 * @return The {@link java.awt.Image image} corresponding to this
	 *         <b>Tile</b>.
	 */
	public Image drawTile() {
		return drawing;
	}

	/**
	 * @return The {@link Visibility visibility} of the current <b>Tile</b>.
	 */
	public Visibility getVisibility() {
		return visibility;
	}

	/**
	 * @return The chance of meeting an {@link characters.Enemy enemy}.
	 */
	public double getEnemyChance() {
		return enemyChance;
	}

	/**
	 * @return The message associated with this <b>Tile</b>.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Resets all the static fields of <b>Tile</b>.
	 */
	public static void reset() {
		letterNum = 0;
		statueNum = 0;
	}
}
