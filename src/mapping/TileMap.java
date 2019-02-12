package mapping;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Stores a map as 2-dimensional field of {@link Tile tiles}. The class
 * processes the drawing of the combination of {@link Tile tiles}. It also
 * provides access to the {@link Tile tiles}.
 * <p>
 * Changes: The class now supports the emptying of {@link Tile tiles} and the
 * unlocking of doors.
 * <p>
 * Total Time Spent: 2 hour(s).
 * 
 * @author Lev Raizman
 * @version 2.0.0.0
 *
 */
public class TileMap {
	/**
	 * The <b>TileMap</b>, represented as a grid of {@link Tile Tiles}.
	 */
	private Tile[][] myMap;

	/**
	 * Creates a <b>TileMap</b> by accessing the .map file associated with the
	 * name.
	 * 
	 * @param mapName
	 *            the name of the <b>TileMap</b> to be created.
	 */
	public TileMap(String mapName) {
		Scanner in;
		int level;

		if (Integer.parseInt(mapName.substring(1)) >= 11) {
			level = 2;
		} else if ("opqrstu".contains(mapName.substring(0, 1))) {
			level = 3;
		} else {
			level = 1;
		}

		try {
			in = new Scanner(new FileReader("resources/maps/" + mapName + ".map"));
			myMap = new Tile[11][11];
			for (int y = 0; y < 11; y++) {
				String temp = in.nextLine();
				for (int x = 0; x < 11; x++) {
					myMap[x][y] = new Tile(temp.charAt(x), level);
				}
			}
			in.close();
		} catch (FileNotFoundException fnfe) {
			System.out.println("file excpetion in tilemap: " + mapName);
		}
	}

	/**
	 * Provides access to a {@link Tile} on the <b>TileMap</b>.
	 * 
	 * @param posX
	 *            x-coordinate of the {@link Tile}
	 * @param posY
	 *            y-coordinate of the {@link Tile}
	 * @return The {@link Tile} at (posX, posY).
	 */
	public Tile getTile(int posX, int posY) {
		return myMap[posX][posY];
	}

	/**
	 * Provides access to a {@link Tile} on the <b>TileMap</b>.
	 * 
	 * @param point
	 *            an (x,y) coordinate on the <b>TileMap</b>.
	 * @return The {@link Tile} at (point.x, point.y).
	 */
	public Tile getTile(Point point) {
		return getTile(point.x, point.y);
	}

	/**
	 * Makes the {@link Tile} at point an empty {@link Tile}, as specified by
	 * new Tile(' ', level);
	 * 
	 * @param point
	 *            the {@link Tile} being changed.
	 * @param level
	 *            the level number of the location of the {@link Tile} being
	 *            changed.
	 */
	public void setTileEmpty(Point point, int level) {
		myMap[point.x][point.y] = new Tile(' ', level);
	}

	/**
	 * Makes the closed door at point become unlocked, as specified by new
	 * Tile('d', level);
	 * 
	 * @param point
	 *            location of the door
	 * @param level
	 *            the level number of the location of the {@link Tile} being
	 *            changed.
	 */
	public void unlockDoor(Point point, int level) {
		myMap[point.x][point.y] = new Tile('d', level);
	}

	/**
	 * @return An {@link Image} that represents the combinations of all the
	 *         {@link Tile Tiles} on the <b>TileMap</b>.
	 */
	public Image drawMap() {
		Image drawing;
		Image row = myMap[0][0].drawTile();
		for (int j = 1; j < 11; j++) {
			row = joinBufferedImage((BufferedImage) row, (BufferedImage) myMap[j][0].drawTile(), 0, true);
		}
		drawing = row;
		for (int i = 1; i < 11; i++) {
			row = myMap[0][i].drawTile();
			for (int j = 1; j < 11; j++) {
				row = joinBufferedImage((BufferedImage) row, (BufferedImage) myMap[j][i].drawTile(), 0, true);
			}
			drawing = joinBufferedImage((BufferedImage) drawing, (BufferedImage) row, 0, false);
		}

		return drawing;
	}

	/**
	 * Combines two images into one.
	 * 
	 * @param img1
	 *            The {@link java.awt.Image images} on the left or top.
	 * @param img2
	 *            The {@link java.awt.Image images} on the right or bottom.
	 * @param offset
	 *            Distance between the two {@link java.awt.Image images}.
	 * @param right
	 *            If true, img2 is connected at the right. If false, img2 is
	 *            connected at the bottom.
	 * @return The {@link java.awt.Image image} created by merging the two
	 *         provided {@link java.awt.Image images}.
	 */
	private static BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2, int offset, boolean right) {
		int wid = img1.getWidth();
		int height = img1.getHeight();
		if (right)
			wid += img2.getWidth() + offset;
		else
			height += img2.getHeight() + offset;
		BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		Color oldColor = g2.getColor();
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, wid, height);
		g2.setColor(oldColor);
		g2.drawImage(img1, null, 0, 0);
		if (right)
			g2.drawImage(img2, null, img1.getWidth() + offset, 0);
		else
			g2.drawImage(img2, null, 0, img1.getHeight() + offset);
		g2.dispose();
		return newImage;
	}
}
